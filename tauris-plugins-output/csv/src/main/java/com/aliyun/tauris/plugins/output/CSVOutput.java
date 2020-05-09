package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.formatter.EventFormatter;
import com.aliyun.tauris.metrics.Counter;
import com.opencsv.CSVWriter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ZhangLei on 17/4/17.
 */
@Name("csv")
public class CSVOutput extends BaseTOutput {

    private static Logger logger = LoggerFactory.getLogger("tauris.output.csv");

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_csv_total").labelNames("id").help("csv output count").create().register();

    @Required
    EventFormatter path;

    int flushInterval = 2;

    String delimiter = ",";
    String lineEnd   = "\n";

    Character quotechar;
    Character escapechar;

    String[] headers = new String[0];

    boolean noHeader = false;

    @Required
    String[] fields;

    /**
     * 若文件在n秒内未写入, 则关闭文件.
     */
    int idleCloseTime = 60;

    private char delimeterChar;

    private final ConcurrentHashMap<File, CSVWriter> writers = new ConcurrentHashMap<>();

    private Thread flushThread;

    public void init() throws TPluginInitException {
        if (headers.length > 0 && headers.length != fields.length) {
            throw new TPluginInitException("field's count not equals to header's count");
        }

        if (delimiter.startsWith("\\u")) {
            delimeterChar = StringEscapeUtils.unescapeEcmaScript(delimiter).toCharArray()[0];
        } else {
            delimeterChar = delimiter.charAt(0);
        }
        lineEnd = StringEscapeUtils.unescapeEcmaScript(lineEnd);

        flushThread = new Thread(() -> {
            while (true) {
                flush();
                try {
                    Thread.sleep(flushInterval * 1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        flushThread.start();
    }

    private File filepath(TEvent event) {
        File filepath = new File(path.format(event));
        File dir      = filepath.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return filepath;
    }

    private CSVWriter getWriter(TEvent event) throws IOException {
        File   filepath = filepath(event);
        CSVWriter w     = writers.get(filepath);
        if (w == null) {
            CSVWriter fw = new CSVWriter(
                    new FileWriter(filepath, true),
                    delimeterChar,
                    quotechar == null ? 0 : quotechar,
                    escapechar == null ? 0 : escapechar,
                    lineEnd);
            if (!filepath.exists() || filepath.length() == 0) {
                if (headers.length > 0 && !noHeader) {
                    fw.writeNext(headers);
                } else if (headers.length == 0 && !noHeader) {
                    fw.writeNext(fields);
                }
            }
            w = writers.putIfAbsent(filepath, fw);
            if (w == null) {
                w = fw;
            }
        } else if(!filepath.exists()) {
            IOUtils.closeQuietly(w);
            writers.remove(filepath);
            w = getWriter(event);
        }
        return w;
    }

    @Override
    public void doWrite(TEvent event) {
        try {
            String[] vs = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Object v = event.get(fields[i]);
                vs[i] = v == null ? "" : v.toString();
            }
            synchronized (writers) {
                CSVWriter w = getWriter(event);
                w.writeNext(vs);
            }
            OUTPUT_COUNTER.labels(id()).inc();
        } catch (IOException e) {
            logger.error("write file error", e);
        }
    }

    public void close() {
        flushThread.interrupt();
        Set<File> ks = new HashSet<>(writers.keySet());
        for (File k : ks) {
            CSVWriter w = writers.remove(k);
            IOUtils.closeQuietly(w);
        }
    }

    public void flush() {
        for (CSVWriter w : writers.values()) {
            try {
                w.flush();
            } catch (IOException e) {
            }
        }
        for (File f : writers.keySet()) {
            if (System.currentTimeMillis() - f.lastModified() > idleCloseTime * 1000) {
                synchronized (writers) {
                    IOUtils.closeQuietly(writers.get(f));
                    writers.remove(f);
                }
            }
        }
    }
}
