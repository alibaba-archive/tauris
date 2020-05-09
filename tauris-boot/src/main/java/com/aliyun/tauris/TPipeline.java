package com.aliyun.tauris;

import com.aliyun.tauris.pipeline.*;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class TPipeline {

    public enum State {
        starting, running, failed, closing, closed;
    }

    public static final String DEFAULT_EVENT_FACTORY = "default";

    public static final String SYSPROP_EVENT_FACTORY            = "tauris.event.factory";
    public static final String SYSPROP_FILTER_PIPE_CAPACITY     = "tauris.filter.pipe.capacity";
    public static final String SYSPROP_OUTPUT_PIPE_CAPACITY     = "tauris.output.pipe.capacity";
    public static final String SYSPROP_DISTRIBUTE_PIPE_CAPACITY = "tauris.distribute.pipe.capacity";
    public static final String SYSPROP_OUTPUT_DISTRIBUTE_MODE   = "tauris.output.distribute.mode";
    public static final String SYSPROP_FILTER_WORKERS           = "tauris.filter.workers";

    private TLogger logger;

    private volatile State             state;
    private          List<InputWorker> inputWorkers;

    private TPipe<TEvent> pipe;

    public TPipeline() throws TPluginInitException {
        logger = TLogger.getLogger(this);
    }

    /**
     * @param inputs             输出插件
     * @param filters            过滤器插件
     * @param outputs            输出插件
     * @param filterWorkerCount  filter worker数量
     * @param filterPipeCapacity filter 队列容量
     * @param outputPipeCapacity output 队列容量
     * @throws TPluginInitException
     */
    public void configure(List<TInputGroup> inputs,
                          List<TFilterGroup> filters,
                          int filterWorkerCount,
                          int filterPipeCapacity,
                          boolean distributeGrouped,
                          int distributePipeCapacity,
                          List<TOutputGroup> outputs,
                          int outputPipeCapacity) throws TPluginInitException {
        TEventFactory eventFactory = createEventFactory();
        this.pipe = new PipesBuilder()
                .withFilters(filters, filterWorkerCount, filterPipeCapacity)
                .withDistribute(distributeGrouped, distributePipeCapacity)
                .withOutput(outputs, outputPipeCapacity)
                .build();
        inputWorkers = new ArrayList<>();
        for (TInputGroup group : inputs) {
            for (TInput input : group.getInputs()) {
                inputWorkers.add(new InputWorker(eventFactory, input, pipe));
            }
        }
    }

    public boolean start() {
        state = State.starting;
        try {
            pipe.open();
            state = State.running;
            startInputs();
            logger.INFO("pipeline start success");
            return true;
        } catch (Exception e) {
            logger.EXCEPTION("pipeline start failed", e);
            closeInputs();
            pipe.close();
            state = State.failed;
            return false;
        }
    }

    public void close() {
        state = State.closing;
        closeInputs();
        try {
            pipe.close();
        } catch (Exception e) {
            logger.EXCEPTION("pipe closed error", e);
        }
        state = State.closed;
        logger.INFO("pipeline closed");
    }

    public State getState() {
        return state;
    }

    private void startInputs() {
        for (InputWorker worker : inputWorkers) {
            worker.start();
        }
    }

    private void closeInputs() {
        List<Thread> stopThreads = Lists.transform(inputWorkers, (t) -> new Thread(() -> {
            logger.INFO("input plugin %s closing", t.getInput().id());
            try {
                t.shutdown();
                logger.INFO("input plugin %s closed", t.getInput().id());
            } catch (RuntimeException e) {
                logger.ERROR("input plugin %s close failed", e, t.getInput().id());
            }
        }));
        stopThreads.forEach(Thread::start);
        stopThreads.forEach((thread) -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        });
        logger.INFO("all input plugins has been closed");
    }

    protected static TEventFactory createEventFactory() {
        String                       factoryName = System.getProperty(SYSPROP_EVENT_FACTORY, DEFAULT_EVENT_FACTORY);
        ServiceLoader<TEventFactory> factories   = ServiceLoader.load(TEventFactory.class);
        for (TEventFactory factory : factories) {
            if (factory.getName().equalsIgnoreCase(factoryName)) {
                return factory;
            }
        }
        throw new Error("no such event factory:" + factoryName);
    }

}
