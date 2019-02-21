package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metric.Gauge;
import com.aliyun.tauris.plugins.input.tailer.*;
import com.aliyun.tauris.utils.TLogger;
import com.aliyun.tauris.utils.Wildcard;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 16/12/9.
 */
public class TailerInput extends BaseTInput {

    private static Gauge TAILER_POS = Gauge.build().name("input_tailer_position").labelNames("id", "filename").help("tailer current read position").create().register();

    private TLogger logger;

    @Required
    File dir;

    Pattern pattern;

    String filename;

    File statFile;

    long idleClosedMillis = 60000;

    private Thread lookupThread;

    private ConcurrentHashMap<File, Tailer> tailers = new ConcurrentHashMap<>();

    private volatile boolean running = false;

    private FileManager fileManager;

    public void doInit() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        if (filename != null) {
            pattern = Wildcard.wildcardToRegex(filename);
        }
        if (pattern == null) {
            throw new TPluginInitException("pattern or filename must be set");
        }
        lookupThread = new Thread(() -> {
            while (running) {
                this.lookup();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        lookupThread.setDaemon(true);

        dir = new File(dir.getAbsolutePath());
        fileManager = new FileManager(dir, pattern, idleClosedMillis, statFile);
        try {
            fileManager.init();
        } catch (IOException e) {
            throw new TPluginInitException("tailer init failed", e);
        }
        fileManager.onFileClose((f) -> {
            if (tailers.containsKey(f)) {
                tailers.get(f).stop();
            }
            logger.INFO("tailer for file " + f + " stopped");
            tailers.remove(f);
        });
    }

    @Override
    public void run() throws Exception {
        running = true;
        lookup();
        lookupThread.start();
    }

    @Override
    public void close() {
        super.close();
        for (Tailer tailer : tailers.values()) {
            tailer.stop();
            logger.INFO("tailer for file %s stopped", tailer.getFile());
        }
        running = false;
        lookupThread.interrupt();
        try {
            fileManager.savePositions();
        } catch (Exception e) {
            logger.ERROR("tailer input dump file position failed", e);
        }
        logger.INFO("tailer input closed");
    }

    private Tailer createTailer(File file, long position) throws IOException {
        String filepath = file.getAbsolutePath();
        logger.INFO("%s opened on %d", filepath, position);
        return StdTailer.create(file, new TailListener(), 200, position);
    }

    private void storeFileReadPosition(File file, long position) {
        TAILER_POS.labels(id(), file.getName()).set(position);
        fileManager.snapPosition(file, position);
    }

    /**
     * 每个秒在目标文件夹下检查新文件
     * 如果发现新文件产生, 若文件的创建时间晚于上一次lookup新文件的时间, 则从文件头部打开, 否则从文件尾部打开.
     */
    private void lookup() {
        try {
            if (!dir.exists()) {
                //close all
                tailers.clear();
            } else {
                List<FileManager.FileStat> records = fileManager.listNewFiles();
                for (FileManager.FileStat record : records) {
                    if (!tailers.containsKey(record.getFile())) { // new file
                        Tailer tailer = createTailer(record.getFile(), record.getPosition());
                        tailers.put(record.getFile(), tailer);
                    }
                }
            }
        } catch (Exception e) {
            logger.EXCEPTION(e);
        }
    }

    class TailListener extends TailerListenerAdapter {

        private File file;

        @Override
        public void init(Tailer tailer) {
            this.file = tailer.getFile();
            logger.INFO("tailer input file %s has been opened", file.getAbsolutePath());
        }

        @Override
        public void fileClosed(File file) {
            logger.INFO("tailer input file %s has been closed", file.getAbsolutePath());
            TAILER_POS.remove(id(), file.getName());
        }

        @Override
        public void handle(String line, long pos) {
            storeFileReadPosition(file, pos);
            if (line.trim().isEmpty()) {
                return;
            }
            try {
                TEvent event = codec.decode(line);
                event.addMeta("filename", file.getAbsolutePath());
                putEvent(event);
            } catch (DecodeException e) {
                logger.WARN2("decode error", e, line);
            } catch (Exception e) {
                logger.EXCEPTION(e);
            }
        }

        @Override
        public void handle(File file, Exception ex) {
            logger.INFO("tail input file %s raise an exception", ex, file.getAbsolutePath());
        }
    }
}
