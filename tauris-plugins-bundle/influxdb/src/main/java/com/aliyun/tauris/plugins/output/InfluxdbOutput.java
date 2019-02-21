package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metric.Counter;
import com.aliyun.tauris.metric.Gauge;
import com.aliyun.tauris.plugins.output.influxdb.InfluxDBException;
import com.aliyun.tauris.plugins.output.influxdb.InfluxDBFactory;
import com.aliyun.tauris.plugins.output.influxdb.InfluxDBIOException;
import com.google.common.util.concurrent.AtomicDouble;
import okhttp3.*;
import org.apache.commons.collections4.keyvalue.MultiKey;
import com.aliyun.tauris.plugins.output.influxdb.InfluxDB;
import com.aliyun.tauris.plugins.output.influxdb.dto.BatchPoints;
import com.aliyun.tauris.plugins.output.influxdb.dto.Point;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.*;

import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.JobBuilder.*;

import static com.aliyun.tauris.plugins.output.influxdb.dto.BatchPoints.*;

@Name("influxdb")
public class InfluxdbOutput extends BaseTOutput {

    public enum RunMode {
        flow, aggr
    }

    private static final DateTimeFormatter WAL_FILEPATH_PATTERN = DateTimeFormat.forPattern("yyyy/MM/dd/yyyyMMddHHmmss");

    private static Counter OUTPUT_COUNTER  = Counter.build().name("output_influxdb_total").labelNames("id", "measurement").help("influxdb output count").create().register();
    private static Counter ERROR_COUNTER   = Counter.build().name("output_influxdb_error_total").labelNames("id").help("influxdb error count").create().register();
    private static Counter LATENCY_COUNTER = Counter.build().name("output_influxdb_latency_total").labelNames("id").help("influxdb latency write event count").create().register();
    private static Gauge   SERIES_GUAGE    = Gauge.build().name("output_influxdb_series").labelNames("id", "measurement").help("influxdb series count").create().register();
    private static Gauge   WRITER_GUAGE    = Gauge.build().name("output_influxdb_writer_thread").labelNames("id", "measurement").help("influxdb writer thread count").create().register();

    private static Logger logger = LoggerFactory.getLogger(InfluxdbOutput.class);

    /**
     * url为空则只写wal不写influxdb
     */
    String url;

    String username = "root";

    String password = "root";

    @Required
    String database;

    @Required
    String measurement;

    String retentionPolicy;

    @Required
    String flushCronExpr;

    /**
     * 输出的tag, 值可以是:分隔的字符串, :前是tag名, :后是event的field名
     */
    @Required
    String[] tagFields;

    /**
     * 输出的value, 值可以是:分隔的字符串, :前是value名, :后是event的field名
     */
    @Required
    String[] valueFields;

    /**
     * 在输出之前在此目录中写入数据文件，measurement为子目录。输出路径为
     * ${wafDir}/%{+yyyyMMddHHmmss}.dat
     */
    File walDir;

    RunMode runMode = RunMode.flow;

    String interval = "1m";

    int connectTimeout = 10;
    int readTimeout    = 180;
    int writeTimeout   = 180;

    int maxSnapCount = 10; // 缓存中最大保存多少个周期的snap数据

    int writerThreadCount = 100;

    int retryTimes    = 6; //写入失败后的重试次数
    int retryInterval = 10; //写入失败后重试间隔时间

    /**
     * 为true时，value为0也输出。默认true
     */
    boolean outputZero = true;

    File snapshotFile;

    private TagField[] _tagFields;

    private ValueField[] _valueFields;

    private Interval _interval;

    /**
     * key time
     */
    private SnapStore snapStore = new SnapStore();

    private Scheduler scheduler;

    private ThreadPoolExecutor writeService;

    private Lock lock = new ReentrantLock();

    private Lock walLock = new ReentrantLock();

    private BasicAuthenticator _authenticator;

    public void init() throws TPluginInitException {
        this._interval = Interval.from(interval);
        this._tagFields = new TagField[tagFields.length];
        for (int i = 0; i < tagFields.length; i++) {
            this._tagFields[i] = new TagField(tagFields[i]);
        }
        this._valueFields = new ValueField[valueFields.length];
        for (int i = 0; i < valueFields.length; i++) {
            this._valueFields[i] = new ValueField(valueFields[i]);
        }

        if (url != null) {
            try {
                URL u = new URL(url);
                String userInfo = u.getUserInfo();
                if (userInfo != null) {
                    url = url.replaceAll(userInfo + "@", "");
                    _authenticator = new BasicAuthenticator(userInfo);
                }
            } catch (MalformedURLException e) {
                throw new TPluginInitException("invalid url:" + url);
            }
        }
    }

    @Override
    public void start() throws Exception {
        if (walDir != null) {
            if (!walDir.exists() && !walDir.mkdirs()) {
                throw new TPluginInitException("waf_dir " + walDir.getAbsolutePath() + " not exists and cannot be created");
            }
        }
        if (runMode == RunMode.aggr && snapshotFile != null && snapshotFile.exists()) {
            try {
                snapStore.loadFromFile(snapshotFile);
            } catch (Exception ex) {
                logger.warn(String.format("WARN[%s] load snap map serialize image error", id()), ex);
            }
        }

        writeService = (ThreadPoolExecutor) Executors.newFixedThreadPool(writerThreadCount);

        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        scheduler = schedFact.getScheduler();
        JobDataMap data = new JobDataMap();
        data.put("instance", this);
        JobDetail job     = newJob(FlushJob.class).usingJobData(data).build();
        Trigger   trigger = newTrigger().withSchedule(cronSchedule(flushCronExpr)).build();
        scheduler.getContext().put("instance", this);
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
    }

    @Override
    public void doWrite(TEvent event) {
        long currentTimeMode = System.currentTimeMillis() / 1000 / _interval.seconds();
        long eventTimeMode  = event.getTimestamp().getMillis() / 1000 / _interval.seconds();
        if (eventTimeMode - currentTimeMode > 2) {
            // logger.warn("event's timestamp too early, timestamp is " + event.getTimestamp());
            return;
        }
        if (currentTimeMode - eventTimeMode > maxSnapCount) {
            LATENCY_COUNTER.labels(id()).inc();
            return;
        }
        EventPoint ep = createEventPoint(event);
        if (ep == null) {
            return;
        }
        snapStore.addPoint(ep, 1); //最后一个时间点数据不完整，在流量图上会显示为下降，所以给point的时间加上一个周期
    }

    public void flush() {
        flush(false);
    }

    private void flush(boolean async) {
        if (runMode == RunMode.flow) {
            flushOnFlowMode(async);
        }
        if (runMode == RunMode.aggr) {
            flushOnAggrMode(async);
        }
    }

    private void flushOnFlowMode(boolean async) {
        Map<Long, Snap> snapMap = snapStore.getAndClear();
        try {
            flushSnapMap(async, snapMap);
            for (Map.Entry<Long, Snap> e : snapMap.entrySet()) {
                SERIES_GUAGE.labels(id(), measurement).dec(e.getValue().size());
            }
        } catch (Exception e) {
            logger.error("unexpected exception", e);
        }
    }

    private void flushOnAggrMode(boolean async) {
        lock.lock();
        try {
            Map<Long, Snap> snapMap = snapStore.get();
            flushSnapMap(async, snapMap);
            int c = snapStore.expire(maxSnapCount);
            SERIES_GUAGE.labels(id(), measurement).dec(c);
        } catch (Exception e) {
            logger.error("unexpected exception", e);
        } finally {
            lock.unlock();
        }
    }

    private void flushSnapMap(boolean async, Map<Long, Snap> snapMap) {
        snapMap.forEach((ts, snap) -> {
            BatchPoints batchPoints = snap.toPoints();
            if (!batchPoints.getPoints().isEmpty()) {
                snap.reset();
                if (async) {
                    writeService.execute(new InfluxWriteJob(ts, batchPoints));
                    WRITER_GUAGE.labels(id(), measurement).inc();
                } else {
                    writeToInflux(batchPoints, ts);
                }
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
        try {
            scheduler.shutdown();
            flush();
            if (snapshotFile != null) {
                try {
                    snapStore.dumpToFile(snapshotFile);
                } catch (IOException ex) {
                    logger.warn(String.format("WARN[%s] dump snap map error", id()), ex);
                }
            }
            writeService.shutdownNow();
            writeService.awaitTermination(3000, TimeUnit.MICROSECONDS);
        } catch (SchedulerException | InterruptedException e) {
            logger.error("shutdown failed", e);
        }
    }

    private InfluxDB connect(String url, String username, String password) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (_authenticator != null) {
            builder.authenticator(_authenticator);
            builder.addInterceptor(_authenticator); //如果不加这行okhttp的第一次发送请求时不带Authorization头部，在server返回401时才会再次发送。为了避免重复发送请求，在请求头部默认带上Authorization
        }
        builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        return InfluxDBFactory.connect(url, username, password, builder);
    }

    private EventPoint createEventPoint(TEvent event) {
        long     time = event.getTimestamp().getMillis();
        String[] ks   = new String[_tagFields.length];
        for (int i = 0; i < _tagFields.length; i++) {
            ks[i] = _tagFields[i].tagOf(event);
        }
        Tags     tags   = new Tags(ks);
        Double[] values = new Double[_valueFields.length];
        for (int i = 0; i < _valueFields.length; i++) {
            Double value = _valueFields[i].valueOf(event);
            values[i] = value;
        }
        return new EventPoint(time, tags, values);
    }

    private EventPoint createEventPoint(Point point) {
        long                time = TimeUnit.MILLISECONDS.convert(point.getTime(), point.getPrecision());
        Map<String, String> tags = point.getTags();
        String[]            ks   = new String[_tagFields.length];
        for (int i = 0; i < _tagFields.length; i++) {
            ks[i] = tags.get(_tagFields[i].tagName);
        }

        Map<String, Object> fields = point.getFields();

        Double[] values = new Double[_valueFields.length];
        for (int i = 0; i < _valueFields.length; i++) {
            values[i] = (Double) fields.get(_valueFields[i].valueName);
        }
        return new EventPoint(time, new Tags(ks), values);
    }

    private File getWalFile() {
        if (walDir != null) {
            String prefix = DateTime.now().toString(WAL_FILEPATH_PATTERN);
            File file = new File(walDir, prefix);
            File dir = file.getParentFile();
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    logger.error("cannot make wal dir " + dir.getName() + "");
                    return null;
                }
            }

            int idx = 0;
            while (true) {
                file = new File(walDir, prefix + "." + idx + ".dat");
                if (!file.exists()) {
                    return file;
                }
                idx++;
            }
        }
        return null;
    }

    private void writeToInflux(BatchPoints points, long timestamp) {
        walLock.lock();
        File walFile = getWalFile();
        if (walFile != null) {
            try (FileWriter fw = new FileWriter(walFile)) {
                for (Point p : points.getPoints()) {
                    fw.write(p.lineProtocol());
                    fw.write('\n');
                }
            } catch (IOException e) {
                logger.error("write wal file " + walFile.getName() + " failed, cause by " + e.getMessage());
            }
        }
        walLock.unlock();
        if (url != null) {
            int times = 1;
            while (times <= retryTimes) {
                InfluxDB influxDB = connect(url, username, password);
                boolean retry = false;
                try {
                    influxDB.write(points);
                    OUTPUT_COUNTER.labels(id(), measurement).inc(points.getPoints().size());
                    logger.info(String.format("flush %s series on %s", points.getPoints().size(), new Date(timestamp * _interval.seconds() * 1000)));
                } catch (InfluxDBIOException e) {
                    retry = true;
                    logger.error("write into influxdb failed, raise an io exception" + "," + times + "/10,id:" + id(), e);
                } catch (InfluxDBException e) {
                    retry = true;
                    if (e.getCode() >= 300 && e.getCode() < 500) {
                        logger.error("write into influxdb failed, please check nginx config, http status is " + e.getCode() + "," + times + "/10,id:" + id());
                    }
                    if (e.getCode() > 500) {
                        logger.error("write into influxdb failed, cause by server fault, http status is " + e.getCode() + "," + times + "/10,id" + id());
                    }
                } finally {
                    influxDB.close();
                }
                if (retry) {
                    times++;
                    try {
                        Thread.sleep(retryInterval * 1000);
                    } catch (InterruptedException ex) {
                    }
                } else {
                    break;
                }
            }
            if (times == 10) {
                ERROR_COUNTER.labels(id()).inc(points.getPoints().size());
            }
        }
    }


    public static class FlushJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                ((InfluxdbOutput) context.getJobDetail().getJobDataMap().get("instance")).flush(true);
            } catch (Exception e) {
                logger.error("flush exception", e);
            }
        }
    }

    private class SnapStore {

        private ConcurrentHashMap<Long, Snap> snapMap = new ConcurrentHashMap<>();

        public void loadFromFile(File file) throws IOException {
            FileReader reader = null;
            try {
                reader = new FileReader(file);
                Scanner scanner = new Scanner(reader);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    try {
                        Point point = Point.parseLine(line, TimeUnit.NANOSECONDS);
                        addPoint(createEventPoint(point), 0);
                    } catch (IllegalArgumentException e) {
                        //ignore
                    }
                }
                this.reset();
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }

        public void dumpToFile(File file) throws IOException {
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                for (Snap snap : snapMap.values()) {
                    snap.dump(writer);
                }
            } finally {
                IOUtils.closeQuietly(writer);
            }
        }

        public void addPoint(EventPoint point, int unitOffset) {
            lock.lock();
            try {
                long mode = (point.time / 1000 / _interval.seconds()) + unitOffset;
                Snap snap = snapMap.get(mode);
                if (snap == null) {
                    snap = new Snap(mode * _interval.seconds());
                    Snap v = snapMap.putIfAbsent(mode, snap);
                    if (v != null) {
                        snap = v;
                    }
                }
                snap.put(point);
            } finally {
                lock.unlock();
            }
        }

        public Map<Long, Snap> getAndClear() {
            Map<Long, Snap> nSnapMap = null;
            lock.lock();
            nSnapMap = snapMap;
            snapMap = new ConcurrentHashMap<>();
            lock.unlock();
            return nSnapMap;
        }

        public Map<Long, Snap> get() {
            return snapMap;
        }

        /**
         * 删除最早N个周期的数据, 使缓存中的数据不超过keeyCount个周期
         * @param keepCount
         * @return 清理掉数据的数量
         */
        public int expire(int keepCount) {
            int c = 0;
            if (snapMap.size() >= keepCount) {
                List<Long> ts = new ArrayList<>(snapMap.keySet());
                ts.sort(Long::compare);
                while (ts.size() >= keepCount) {
                    Snap snap = snapMap.remove(ts.remove(0));
                    c += snap.size();
                }
            }
            return c;
        }

        public void reset() {
            snapMap.values().forEach(Snap::reset);
        }
    }

    private class Snap implements java.io.Serializable {

        private static final long serialVersionUID = 5625251240429179339L;

        private long timestamp;
        private ConcurrentHashMap<Tags, AggregatedPoint> data = new ConcurrentHashMap<>();

        public Snap() {
        }

        public Snap(long timestamp) {
            this.timestamp = timestamp;
        }

        public void put(EventPoint ep) {
            AggregatedPoint point = data.get(ep.tags);
            if (point == null) {
                point = new AggregatedPoint(ep.tags);
                AggregatedPoint v = data.putIfAbsent(ep.tags, point);
                if (v != null) {
                    point = v;
                } else {
                    SERIES_GUAGE.labels(id(), measurement).inc();
                }
            }
            for (int i = 0; i < _valueFields.length; i++) {
                String valueName = _valueFields[i].valueName;
                Double value = ep.values[i];
                if (value != null) {
                    point.incr(valueName, value);
                }
            }
        }

        public BatchPoints toPoints() {
            BatchPoints batchPoints = database(database).retentionPolicy(retentionPolicy).consistency(InfluxDB.ConsistencyLevel.ALL).build();
            data.forEach((tags, sp) -> {
                if (sp.isModified()) {
                    Point.Builder p = Point.measurement(measurement).time(timestamp, TimeUnit.SECONDS);
                    tags.export(p);
                    sp.export(p);
                    batchPoints.point(p.build());
                }
            });
            return batchPoints;
        }

        public int size() {
            return data.size();
        }

        public void reset() {
            data.values().forEach(AggregatedPoint::reset);
        }

        public void clear() {
            data.clear();
        }

        public void dump(FileWriter writer) throws IOException {
            for (Map.Entry<Tags, AggregatedPoint> e : data.entrySet()) {
                Point.Builder p = Point.measurement(measurement).time(timestamp, TimeUnit.SECONDS);
                e.getKey().export(p);
                e.getValue().export(p);
                writer.write(p.build().lineProtocol());
                writer.write("\n");
            }
        }
    }

    private class Tags extends MultiKey<String> {

        private static final long serialVersionUID = 4361969037645922494L;

        public Tags(String[] keys) {
            super(keys);
        }

        public void export(Point.Builder pb) {
            for (int i = 0; i < _tagFields.length; i++) {
                String tv = getKey(i);
                if (tv != null) {
                    pb.tag(_tagFields[i].tagName, tv);
                }
            }
        }
    }

    private class EventPoint implements java.io.Serializable {

        private static final long serialVersionUID = 8741864643820117403L;

        private long time;

        private Tags tags;

        private Double[] values;

        public EventPoint(long time, Tags tags, Double[] values) {
            this.time = time;
            this.tags = tags;
            this.values = values;
        }
    }

    private static class TagField implements java.io.Serializable {

        private static final long serialVersionUID = 4693987030623130545L;

        private String tagName;
        private String field;

        public TagField(String expr) {
            if (expr.contains(":")) {
                String[] ps = expr.split(":");
                this.tagName = ps[0];
                this.field = ps[1];
            } else {
                this.tagName = expr;
                this.field = expr;
            }
        }

        public String tagOf(TEvent event) {
            Object o = event.get(field);
            return o == null ? null : o.toString();
        }
    }


    private static class ValueField implements java.io.Serializable {

        private static final long serialVersionUID = 4466038569265111026L;

        private String valueName;
        private String field;

        public ValueField(String expr) {
            if (expr.contains(":")) {
                String[] ps = expr.split(":");
                this.valueName = ps[0];
                this.field = ps[1];
            } else {
                this.valueName = expr;
                this.field = expr;
            }
        }

        public Double valueOf(TEvent event) {
            Object value = event.get(field);
            if (value == null) {
                return null;
            }
            if (!(value instanceof Number)) {
                throw new IllegalArgumentException(String.format("%s's value is %s(%s), not a number", field, value, value.getClass()));
            }
            Number v = (Number) event.get(field);
            if (v != null) {
                return v.doubleValue();
            } else {
                return null;
            }
        }
    }

    private class AggregatedPoint implements java.io.Serializable {

        private static final long serialVersionUID = -1336871589328704699L;

        private Tags tags;

        private ConcurrentHashMap<String, AtomicDouble> values;

        private boolean modified = false;

        public AggregatedPoint(Tags tags) {
            this.tags = tags;
            this.values = new ConcurrentHashMap<>();
            for (ValueField value : _valueFields) {
                values.put(value.valueName, new AtomicDouble(0L));
            }
        }

        public void incr(String valueName, double value) {
            values.get(valueName).addAndGet(value);
            modified = true;
        }

        public void export(Point.Builder pb) {
            tags.export(pb);
            values.forEach((valueName, value) -> {
                double v = value.doubleValue();
                if (outputZero || v > 0) {
                    pb.addField(valueName, value.doubleValue());
                }
            });
        }

        public void reset() {
            modified = false;
        }

        public boolean isModified() {
            return modified;
        }
    }

    private class InfluxWriteJob implements Runnable {
        private long        timestamp;
        private BatchPoints points;

        public InfluxWriteJob(long timestamp, BatchPoints points) {
            this.timestamp = timestamp;
            this.points = points;
        }

        @Override
        public void run() {
            writeToInflux(points, timestamp);
            WRITER_GUAGE.labels(id(), measurement).dec();
        }
    }

    private enum IntervalUnit {
        s(1), m(60), h(3600), d(24 * 3600);

        public final int seconds;

        IntervalUnit(int seconds) {
            this.seconds = seconds;
        }
    }

    private static class Interval {

        private int          value;
        private IntervalUnit unit;

        private static Interval from(String expr) {
            Pattern                 p = Pattern.compile("^([\\d]+)(s|m|h|d)$");
            java.util.regex.Matcher m = p.matcher(expr);
            if (!m.matches()) {
                throw new IllegalArgumentException("invalid interval:" + expr);
            }
            Interval i = new Interval();
            i.value = Integer.parseInt(m.group(1));
            i.unit = IntervalUnit.valueOf(m.group(2));
            return i;
        }

        public int seconds() {
            return value * unit.seconds;
        }

        public String toString() {
            return value + unit.name();
        }
    }

    private static class BasicAuthenticator implements Authenticator, Interceptor {

        private final String credential;

        public BasicAuthenticator(String userInfo) {
            String username = userInfo;
            String password = "";
            if (userInfo.contains(":")) {
                String[] p = userInfo.split(":");
                username = p[0];
                password = p[1];
            }
            credential = Credentials.basic(username, password);
        }

        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            return response.request().newBuilder()
                    .header("Authorization", credential)
                    .build();
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request         request = chain.request();
            Request.Builder b       = request.newBuilder().header("Authorization", credential);
            return chain.proceed(b.build());
        }
    }
}
