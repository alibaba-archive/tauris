package com.aliyun.tauris;

import com.aliyun.tauris.config.TConfig;
import com.aliyun.tauris.config.TPluginResolverInitializer;
import com.aliyun.tauris.config.parser.Helper;
import com.aliyun.tauris.config.parser.Parser;
import com.aliyun.tauris.config.parser.Pipeline;
import com.aliyun.tauris.metrics.MetricServer;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by ZhangLei on 16/10/20.
 */
public class Tauris {

    private TLogger logger;

    private TPipeline pipeline;

    private TConfig config;

    private MetricServer metricServer;

    private final ReentrantLock lock = new ReentrantLock();

    public Tauris(TConfig config, File...pluginDirs) {
        this.metricServer = MetricServer.createMetricServer();
        this.logger = TLogger.getLogger(this);
        this.config = config;
        TPluginResolverInitializer.initialize(Thread.currentThread().getContextClassLoader(), pluginDirs);
    }

    public void load() throws Exception {
        Pipeline rawPipeline = parse();
        this.pipeline = new TPipeline();
        int     filterWorkerCount      = Integer.parseInt(System.getProperty(TPipeline.SYSPROP_FILTER_WORKERS, "2"));
        int     filterPipeCapacity     = Integer.parseInt(System.getProperty(TPipeline.SYSPROP_FILTER_PIPE_CAPACITY, "0"));
        int     distributePipeCapacity = Integer.parseInt(System.getProperty(TPipeline.SYSPROP_DISTRIBUTE_PIPE_CAPACITY, "0"));
        int     outputPipeCapacity     = Integer.parseInt(System.getProperty(TPipeline.SYSPROP_OUTPUT_PIPE_CAPACITY, "0"));
        boolean distributeGrouped      = System.getProperty(TPipeline.SYSPROP_OUTPUT_DISTRIBUTE_MODE, "group").equals("group");
        this.pipeline.configure(
                rawPipeline.getInputGroups(),
                rawPipeline.getFilterGroups(),
                filterWorkerCount,
                filterPipeCapacity,
                distributeGrouped,
                distributePipeCapacity,
                rawPipeline.getOutputGroups(),
                outputPipeCapacity);
    }

    public void start() throws RuntimeException {
        lock.lock();
        if (metricServer != null) {
            metricServer.init();
            metricServer.start();
        }
        boolean b = pipeline.start();
        lock.unlock();
        if (b) {
            watch();
        } else {
            if (metricServer != null) {
                metricServer.shutdown();
            }
        }
    }

    public void stop() {
        if (lock.isLocked()) {
            logger.INFO("tauris is stopping");
            return;
        }
        try {
            lock.lock();
            logger.INFO("tauris stopping");
            pipeline.close();
            if (metricServer != null) {
                metricServer.shutdown();
            }
            logger.INFO("tauris stopped");
        } finally {
            lock.unlock();
        }
    }

    private void watch() {
        while (true) {
            TPipeline.State state = pipeline.getState();
            if (state == TPipeline.State.failed) {
                stop();
                break;
            }
            if (state == TPipeline.State.closed) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }


    public Pipeline parse() {
        String   cfgText     = config.load();
        Pipeline rawPipeline = Parser.parsePipeline(cfgText);
        rawPipeline.build();
        System.out.println(Helper.m.toString());
        return rawPipeline;
    }
}
