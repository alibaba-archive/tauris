package com.aliyun.tauris;

import com.aliyun.tauris.config.TConfig;
import com.aliyun.tauris.config.parser.Helper;
import com.aliyun.tauris.config.parser.Parser;
import com.aliyun.tauris.config.parser.Pipeline;
import com.aliyun.tauris.metric.MetricServer;
import com.aliyun.tauris.utils.TLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by ZhangLei on 16/10/20.
 */
public class Tauris {

    /**
     * 执行reload时, 允许reload filter 插件
     */
    public static final String SYSPROP_RELOAD_FILTER = "tauris.reload.filter";

    /**
     * 执行reload时, 允许reload output 插件
     */
    public static final String SYSPROP_RELOAD_OUTPUT = "tauris.reload.output";

    private TLogger logger;

    private TPipeline pipeline;

    private TConfig config;

    private MetricServer metricServer;

    private final ReentrantLock lock = new ReentrantLock();

    public Tauris(TConfig config) {
        this.config = config;
        this.metricServer = MetricServer.createMetricServer();
        this.logger = TLogger.getLogger(this);
    }

    public void load() throws Exception {
        Pipeline rawPipeline = parse();
        this.pipeline = new TPipeline(rawPipeline.getInputGroup(), rawPipeline.getFilterGroups(), rawPipeline.getOutputGroups());
    }

    public synchronized void start() throws RuntimeException {
        try {
            lock.lock();
            if (metricServer != null) {
                metricServer.init();
                metricServer.start();
            }
            if (pipeline.start()) {
                watch();
            } else {
                if (metricServer != null) {
                    metricServer.shutdown();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public synchronized void stop() {
        lock.lock();
        logger.INFO("tauris stopping");
        if (metricServer != null) {
            metricServer.shutdown();
        }
        pipeline.close();
        logger.INFO("tauris stopped");
    }

    public void clearPipeline() {
        pipeline.clearQueues();
    }

    public synchronized void reload() {
        if (!lock.isLocked()) {
            lock.lock();
            try {
                logger.INFO("tauris pipeline reloading");
                Pipeline rawPipeline = parse();
                if (System.getProperty(SYSPROP_RELOAD_FILTER, "true").equals("true")) {
                    pipeline.reloadFilters(rawPipeline.getFilterGroups());
                }
                if (System.getProperty(SYSPROP_RELOAD_OUTPUT, "false").equals("true")) {
                    pipeline.reloadOutputs(rawPipeline.getOutputGroups());
                }
                logger.INFO("tauris pipeline reload success");
            } catch (Exception e) {
                logger.ERROR("tauris pipeline reload failed", e);
            } finally {
                lock.unlock();
            }
        }
    }

    private void watch() {
        new Thread(() -> {
            while (true) {
                TPipeline.State state = pipeline.getState();
                if (state == TPipeline.State.failed) {
                    stop();
                    break;
                }
                if (state == TPipeline.State.closing || state == TPipeline.State.closed) {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public Pipeline parse() {
        String   cfgText     = config.load();
        Pipeline rawPipeline = Parser.parsePipeline(cfgText);
        rawPipeline.build();
        System.out.println(Helper.m.toString());
        return rawPipeline;
    }

    public boolean isRunning() {
        return pipeline.getState() == TPipeline.State.running;
    }
}
