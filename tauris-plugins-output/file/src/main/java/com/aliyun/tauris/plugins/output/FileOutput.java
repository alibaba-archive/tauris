package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.*;
import com.aliyun.tauris.formatter.EventFormatter;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.plugins.codec.DefaultPrinterBuilder;
import com.aliyun.tauris.plugins.codec.EncodePrinterBuilder;
import com.aliyun.tauris.plugins.output.file.FormatedFileManager;
import com.aliyun.tauris.plugins.output.file.TFileManager;
import com.aliyun.tauris.TLogger;
import com.google.common.collect.Sets;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class FileOutput extends BaseTOutput {

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_file_total").labelNames("id").help("file output count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_file_error_total").labelNames("id").help("file output error count").create().register();

    private TLogger logger;

    TPrinterBuilder printer = new DefaultPrinterBuilder();

    TEncoder codec;

    EventFormatter path;

    boolean autoFlush     = true;
    int     flushInterval = 2;

    TFileManager filemanager;

    private final Map<File, TPrinter> printers = new ConcurrentHashMap<>();

    private Thread flushThread;

    public void init() throws TPluginInitException {
        logger = TLogger.getLogger(this);
        if (flushInterval > 0) {
            flushThread = new Thread(() -> {
                while (true) {
                    try {
                        flush();
                        Thread.sleep(flushInterval * 1000);
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        logger.EXCEPTION(e);
                    }
                }
            });
        }
        if (codec != null) {
            if (printer instanceof EncodePrinterBuilder) {
                ((EncodePrinterBuilder) printer).setCodec(codec);
            } else {
                throw new TPluginInitException("printer not a EncodePrinterBuilder, codec will be ignored");
            }
        }
        if (filemanager == null && path == null) {
            throw new TPluginInitException("filemanager or path is requried");
        }
        if (filemanager == null) {
            filemanager = new FormatedFileManager(path);
        }
    }

    @Override
    public void start() throws Exception {
        if (flushThread != null) {
            flushThread.start();
        }
    }

    private TPrinter getPrinter(TEvent event) throws IOException {
        File file = filemanager.resolve(event);
        synchronized (printers) {
            TPrinter w = printers.get(file);
            if (w == null) {
                w = printer.create(new FileOutputStream(file, true));
                TPrinter nw = printers.putIfAbsent(file, w);
                if (nw != null) {
                    w = nw;
                }
            }
            return w;
        }
    }

    @Override
    public void doWrite(TEvent event) {
        try {
            TPrinter w = getPrinter(event);
            w.write(event);
            OUTPUT_COUNTER.labels(id()).inc();
        } catch (IOException e) {
            ERROR_COUNTER.labels(id()).inc();
            logger.ERROR("write file error", e);
        } catch (EncodeException e) {
            ERROR_COUNTER.labels(id()).inc();
            logger.ERROR("encode event failed", e);
        }
    }

    public void closePrinter(File file, TPrinter printer) {
        try {
            printer.flush();
            printer.close();
        } catch (IOException e) {
        }
        filemanager.onClose(file);
    }

    @Override
    public void stop() {
        if (flushThread != null) {
            flushThread.interrupt();
        }
        Set<File> ks = new HashSet<>(printers.keySet());
        for (File k : ks) {
            TPrinter w = printers.remove(k);
            closePrinter(k, w);
            logger.INFO("file %s closed", k.getAbsolutePath());
        }
    }

    public void flush() {
        Set<File>                      beRemoved = new HashSet<>();
        Set<Map.Entry<File, TPrinter>> entries   = Sets.newHashSet(printers.entrySet());
        synchronized (printers) {
            for (Map.Entry<File, TPrinter> entry : entries) {
                File f = entry.getKey();
                TPrinter w = entry.getValue();
                if (f.exists()) {
                    if (autoFlush) {
                        try {
                            w.flush();
                        } catch (IOException e) {
                            logger.ERROR("flush file %s error", e, f.getAbsolutePath());
                            beRemoved.add(f);
                        }
                    }
                } else {
                    beRemoved.add(f);
                    continue;
                }
                if (filemanager.canClose(f)) {
                    beRemoved.add(f);
                }
            }
            for (File f : beRemoved) {
                TPrinter printer = printers.remove(f);
                closePrinter(f, printer);
                logger.INFO("file %s closed", f.getAbsolutePath());
            }
        }
    }
}
