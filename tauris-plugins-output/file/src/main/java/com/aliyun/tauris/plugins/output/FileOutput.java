package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.formatter.SimpleFormatter;
import com.aliyun.tauris.metric.Counter;
import com.aliyun.tauris.plugins.codec.DefaultPrinter;
import com.aliyun.tauris.TPrinter;
import com.aliyun.tauris.plugins.output.file.FormatedFileManager;
import com.aliyun.tauris.plugins.output.file.TFileManager;
import com.aliyun.tauris.utils.TLogger;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class FileOutput extends BaseTOutput {

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_file_total").labelNames("id").help("file output count").create().register();

    private TLogger logger;

    TPrinter printer = new DefaultPrinter();

    SimpleFormatter path;

    int flushInterval = 2;

    int outputBufferSize = 8192;

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
                        continue;
                    }
                }
            });
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

    private TPrinter newPrinter(TEvent event) throws IOException, ExecutionException {
        File     file = filemanager.resolve(event);
        TPrinter w    = printers.get(file);
        if (w == null) {
            w = printer.wrap(new BufferedOutputStream(new FileOutputStream(file, true), outputBufferSize)).withCodec(codec);
            TPrinter nw = printers.putIfAbsent(file, w);
            if (nw != null) {
                w = nw;
            }
        }
        return w;
    }

    @Override
    public void doWrite(TEvent event) {
        try {
            TPrinter w = newPrinter(event);
            w.write(event);
            OUTPUT_COUNTER.labels(id()).inc();
        } catch (IOException e) {
            logger.ERROR("write file error", e);
        } catch (ExecutionException e) {
            logger.ERROR("make file error", e);
        } catch (EncodeException e) {
            logger.ERROR("encode event failed", e);
        }
    }

    public void closePrinter(File file, TPrinter printer) {
        IOUtils.closeQuietly(printer);
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
        for (Map.Entry<File, TPrinter> entry : entries) {
            File f = entry.getKey();
            TPrinter w = entry.getValue();
            if (f.exists()) {
                try {
                    w.flush();
                } catch (IOException e) {
                    logger.ERROR("flush file %s error", e, f.getAbsolutePath());
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
