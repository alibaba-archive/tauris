package com.aliyun.tauris.plugins.output;

import com.aliyun.odps.*;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.data.RecordWriter;
import com.aliyun.odps.tunnel.TableTunnel;
import com.aliyun.odps.tunnel.TunnelException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.utils.EventFormatter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ZhangLei on 17/5/28.
 */
@Name("odps")
public class ODPSOutput extends BaseTOutput {

    private static Logger LOG = LoggerFactory.getLogger("tauris.output.odps");

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_odps_total").help("odps put count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_odps_error_total").help("odps put error count").create().register();

    private final static int FLUSH_INTERVAL = 60;  // second

    @Required
    String endPoint;

    @Required
    String accessKeyId;

    @Required
    String accessKeySecret;

    @Required
    String project;

    @Required
    String tablename;

    @Required
    String[] fields;

    int batchSize = 10000;

    String partitionPattern;

    int flushInterval = FLUSH_INTERVAL; // unit second

    private TableTunnel tunnel;

    private EventFormatter partitionSpec;

    private Table table;

    private Map<String, SessionWriter> writers = new HashMap<>(16);
    private Map<String, String> fieldKeyMapping;
    private Odps    odps;

    private ScheduledExecutorService flushService = Executors.newScheduledThreadPool(1);

    private AtomicInteger writeCounter = new AtomicInteger(0);

    public void init() throws TPluginInitException {
        if (partitionPattern != null) {
            partitionSpec = EventFormatter.build(partitionPattern);
        }
        Account account = new AliyunAccount(accessKeyId, accessKeySecret);
        Odps    odps    = new Odps(account);
        odps.setEndpoint(endPoint);
        odps.setDefaultProject(project);
        fieldKeyMapping = new HashMap<>();
        for (String field : fields) {
            String[] group = field.split(":");
            String columnName = group[0];
            String fieldName = columnName;
            if (group.length > 1) {
                fieldName = group[1];
            }
            fieldKeyMapping.put(fieldName, columnName);
        }
    }

    @Override
    public void start() throws Exception {
        super.start();
        try {
            if (!odps.tables().exists(tablename)) {
                throw new TPluginInitException("odps table " + tablename + " not exists");
            }
            tunnel = new TableTunnel(odps);
            table = odps.tables().get(tablename);
            if (partitionSpec != null) {
                for (Partition p : table.getPartitions()) {
                    String part = p.getPartitionSpec().toString();
                    writers.put(part, new SessionWriter(p.getPartitionSpec()));
                }
            } else {
                writers.put("default", new SessionWriter());
            }
        } catch (OdpsException | IOException e) {
            throw new TPluginInitException("init odps failed", e);
        }
        flushService.scheduleAtFixedRate(this::flush, 10, flushInterval, TimeUnit.SECONDS);
    }

    @Override
    protected void doWrite(TEvent event) {
        if (partitionSpec != null) {
            writeWithPartition(event);
        } else {
            writeNoPartition(event);
        }
        if (writeCounter.incrementAndGet() > batchSize) {
            flush();
            writeCounter.set(0);
        }
    }

    protected void writeWithPartition(TEvent event) {
        try {
            String part = partitionSpec.format(event);
            SessionWriter writer;
            if (!writers.containsKey(part)) {
                PartitionSpec spec = new PartitionSpec(part);
                try {
                    table.createPartition(spec);
                } catch (OdpsException e) {
                    //忽略partition已存在异常
                    if (!e.getMessage().contains("AlreadyExistsException")) {
                        throw e;
                    }
                }
                writer = new SessionWriter(spec);
                writers.put(part, writer);
            } else {
                writer = writers.get(part);
            }

            Record r = writer.newRecord();
            for (Map.Entry<String, String> ex : fieldKeyMapping.entrySet()) {
                r.set(ex.getValue(), event.get(ex.getKey()));
            }
            writer.write(r);
        } catch (OdpsException | IOException e) {
            LOG.error(String.format("insert event to %s/%s failed", project, table), e);
        }
    }

    protected void writeNoPartition(TEvent event) {
        try {
            SessionWriter writer = writers.get("default");
            Record r = writer.newRecord();
            for (Map.Entry<String, String> e : fieldKeyMapping.entrySet()) {
                r.set(e.getValue(), event.get(e.getKey()));
            }
            writer.write(r);
        } catch (IOException e) {
            LOG.error(String.format("insert event to %s/%s failed", project, table), e);
        }
    }

    private synchronized void flush() {
        writers.values().stream().forEach(SessionWriter::commit);
    }

    @Override
    public void stop() {
        super.stop();
        flushService.shutdown();
        flush();
        writers.values().stream().forEach(SessionWriter::close);
    }

    private class SessionWriter {
        private          TableTunnel.UploadSession session;
        private          RecordWriter              writer;
        private          PartitionSpec             part;
        private volatile long                      counter;

        public SessionWriter() throws TunnelException, IOException {
            this.init();
        }

        public SessionWriter(PartitionSpec part) throws TunnelException, IOException {
            this.part = part;
            this.init();
        }

        private void init() throws TunnelException, IOException {
            if (part != null) {
                this.session = tunnel.createUploadSession(project, tablename, part);
            } else {
                this.session = tunnel.createUploadSession(project, tablename);
            }
            this.writer = session.openRecordWriter(1);
        }

        public synchronized void write(Record record) throws IOException {
            writer.write(record);
            counter++;
        }

        public Record newRecord() {
            return session.newRecord();
        }

        public synchronized void commit() {
            if (counter == 0) {
                return;
            }
            try {
                LOG.info("flush odps session, " + counter + " records is waiting for commiting");
                IOUtils.closeQuietly(writer);
                session.commit(new Long[]{1l});
                this.init();
                OUTPUT_COUNTER.inc(counter);
                LOG.info("flush odps session success, " + counter + " records is committed for partition " + (part == null ? "default" : part.toString()));
                counter = 0;
            } catch (TunnelException | IOException e) {
                ERROR_COUNTER.inc(counter);
                LOG.error("odps session commit failed", e);
            }
        }

        public void close() {
            commit();
            IOUtils.closeQuietly(writer);
        }

    }
}
