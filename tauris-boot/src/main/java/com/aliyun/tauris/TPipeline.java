package com.aliyun.tauris;

import com.aliyun.tauris.utils.TLogger;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class TPipeline {

    public enum State {
        starting, running, reloading, failed, closing, closed;
    }

    public static final String SYSPROP_FILTER_QUEUE_CAPACITY = "tauris.filter.queue.capacity";
    public static final String SYSPROP_OUTPUT_QUEUE_CAPACITY = "tauris.output.queue.capacity";
    public static final String SYSPROP_FILTER_WORKERS        = "tauris.filter.workers";

    private static final int FILTER_WORKER_COUNT = Integer.parseInt(System.getProperty(SYSPROP_FILTER_WORKERS, "2"));

    private static final int FILTER_QUEUE_CAPACITY = Integer.parseInt(System.getProperty(SYSPROP_FILTER_QUEUE_CAPACITY, "100"));
    private static final int OUTPUT_QUEUE_CAPACITY = Integer.parseInt(System.getProperty(SYSPROP_OUTPUT_QUEUE_CAPACITY, "0"));

    private TLogger logger;

    private volatile TQueue<List<TEvent>> inputToFilter;

    private volatile TQueue<List<TEvent>> filterToOutput;

    private          TInputGroup        inputs;
    private volatile List<TFilterGroup> filters;
    private volatile List<TOutputGroup> outputs;

    private volatile State              state;
    private          List<FilterWorker> filterWorkers;

    private OutputWorkerConnector workerConnector;

    public TPipeline(TInputGroup inputs, List<TFilterGroup> filters, List<TOutputGroup> outputs) throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        this.inputs = inputs;
        this.filters = filters;
        this.outputs = outputs;
        this.inputToFilter = newQueue("input_to_filter", FILTER_QUEUE_CAPACITY);
        this.filterToOutput = newQueue("filter_to_output", OUTPUT_QUEUE_CAPACITY);
    }

    /**
     * reload filter
     *
     * @param filters
     */
    public void reloadFilters(List<TFilterGroup> filters) {
        if (state == State.running) {
            try {
                state = State.reloading;
                List<TFilterGroup> oldFilters = this.filters;
                if (!prepareFilters(filters)) {
                    return;
                }
                this.filters = filters;
                releaseFilters(oldFilters);
            } finally {
                state = State.running;
            }
        } else {
            throw new IllegalStateException("pipeline is still reloading");
        }
    }


    public void close() {
        state = State.closing;
        closeInputs();
        waitQueueEmpty(inputToFilter);
        logger.INFO("input -> filter queue empty");
        waitQueueEmpty(filterToOutput);
        logger.INFO("filter -> output queue empty");
        releaseFilters(filters);
        stopOutput();
        state = State.closed;
        logger.INFO("pipeline closed");
    }

    public void clearQueues() {
        this.inputToFilter.clear();
        logger.INFO("clear %s queue success, queue size is %d", inputToFilter.getName(), inputToFilter.size());
        this.filterToOutput.clear();
        logger.INFO("clear %s queue success, queue size is %d", filterToOutput.getName(), filterToOutput.size());
    }

    public boolean start() {
        state = State.starting;
        if (!startFilter()) {
            logger.ERROR("filter plugin has error");
            stopFilter();
            logger.ERROR("pipeline start failed");
            state = State.failed;
            return false;
        }
        if (!startOutput()) {
            logger.ERROR("output plugin start failed");
            stopFilter();
            stopOutput();
            logger.ERROR("pipeline start failed");
            state = State.failed;
            return false;
        }
        if (!startInputs()) {
            logger.ERROR("input plugin start failed");
            stopFilter();
            stopOutput();
            closeInputs();
            logger.ERROR("pipeline start failed");
            state = State.failed;
            return false;
        }
        state = State.running;
        logger.INFO("pipeline started");
        return true;
    }

    public State getState() {
        return state;
    }

    private boolean startInputs() {
        for (TInput input : inputs.getInputs()) {
            InputWorker worker = new InputWorker(input, inputToFilter);
            worker.start();
        }
        return true;
    }

    private void closeInputs() {
        List<Thread> stopThreads = Lists.transform(this.inputs.getInputs(), (o) -> new Thread(() -> {
            logger.INFO("input plugin %s closing", o.id());
            try {
                o.close();
                PluginTools.release(o);
                logger.INFO("input plugin closed");
            } catch (RuntimeException e) {
                logger.ERROR("input plugin close failed", e);
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

    private boolean startFilter() {
        if (!prepareFilters(filters)) {
            return false;
        }
        filterWorkers = new ArrayList<>();
        for (int i = 0; i < FILTER_WORKER_COUNT; i++) {
            filterWorkers.add(new FilterWorker(inputToFilter, filterToOutput));
        }
        filterWorkers.forEach(Thread::start);
        return true;
    }

    private void stopFilter() {
        if (filterWorkers != null) {
            for (FilterWorker filterWorker : filterWorkers) {
                filterWorker.interrupt();
            }
        }
        this.releaseFilters(filters);
    }

    private boolean prepareFilters(List<TFilterGroup> filters) {
        try {
            for (TFilterGroup fg : filters) {
                fg.prepare();
            }
        } catch (TPluginInitException e) {
            logger.ERROR(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void releaseFilters(List<TFilterGroup> filters) {
        for (TFilterGroup fg : filters) {
            fg.release();
        }
    }

    private boolean startOutput() {
        workerConnector = new OutputWorkerConnector(filterToOutput);
        List<OutputWorker> workers = new ArrayList<>();
        for (TOutputGroup o : outputs) {
            workers.addAll(Lists.transform(o.getOutputs(), OutputWorker::new));
        }
        workerConnector.connect(workers);
        try {
            for (OutputWorker worker : workers) {
                worker.startup();
            }
            workerConnector.startup();
        } catch (Exception e) {
            logger.ERROR("output start failed", e);
            return false;
        }
        logger.INFO("all output plugins has been started");
        return true;
    }


    public void reloadOutputs(List<TOutputGroup> outputs) {
        if (state == State.running) {
            try {
                state = State.reloading;
                List<OutputWorker> oldWorkers = workerConnector.workers;
                List<OutputWorker> newWorkers = new ArrayList<>();
                for (TOutputGroup o : outputs) {
                    newWorkers.addAll(Lists.transform(o.getOutputs(), OutputWorker::new));
                }
                for (OutputWorker worker : newWorkers) {
                    worker.startup();
                }
                workerConnector.connect(newWorkers);
                this.outputs = outputs;

                for (OutputWorker worker : oldWorkers) {
                    worker.shutdown();
                }
            } catch (Exception e) {
                logger.ERROR("reload output failed", e);
            } finally {
                state = State.running;
            }
        } else {
            throw new IllegalStateException("pipeline is still reloading");
        }
    }

    private void stopOutput() {
        if (workerConnector != null) {
            workerConnector.shutdown();
        }
        logger.INFO("all output plugins has been stopped");
    }

    private <T> TQueue<T> newQueue(String name, int size) {
        return new DefaultQueue<T>(name, size);
    }

    private void waitQueueEmpty(TQueue<?> queue) {
        while (true) {
            if (queue.isEmpty()) {
                break;
            }
        }
    }

    class InputWorker extends Thread {

        private TQueue<List<TEvent>> queue;

        private TInput input;

        public InputWorker(TInput input, TQueue<List<TEvent>> queue) {
            this.queue = queue;
            this.input = input;
        }

        @Override
        public void run() {
            try {
                input.init(queue);
                input.run();
            } catch (Exception e) {
                logger.ERROR("input plugin thread raise an exception, pipeline will be closed", e);
                state = TPipeline.State.failed;
            }
        }
    }

    class FilterWorker extends Thread {

        private TQueue<List<TEvent>> inQueue;
        private TQueue<List<TEvent>> outQueue;

        public FilterWorker(TQueue<List<TEvent>> inQueue, TQueue<List<TEvent>> outQueue) {
            this.inQueue = inQueue;
            this.outQueue = outQueue;
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    List<TEvent> events = inQueue.take();
                    List<TEvent> newEvents = new LinkedList<>();
                    for (TEvent event : events) {
                        for (TFilterGroup fg : filters) {
                            event = fg.filter(event);
                            if (event == null) {
                                break;
                            }
                        }
                        if (event != null) {
                            newEvents.add(event);
                        }
                    }
                    if (!newEvents.isEmpty()) {
                        outQueue.put(newEvents, newEvents.size());
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
            logger.warn("filter worker quited");
        }
    }

    class OutputWorker extends Thread {

        private final    TOutput              output;
        private final    TQueue<List<TEvent>> queue;
        private volatile boolean              running;

        public OutputWorker(TOutput output) {
            this.output = output;
            this.queue = newQueue(String.format("output_queue_%s", output.id()), OUTPUT_QUEUE_CAPACITY);
            setDaemon(true);
        }

        @Override
        public void run() {
            while (running) {
                try {
                    List<TEvent> events = queue.take();
                    if (events.isEmpty()) {
                        continue;
                    }
                    for (TEvent event : events) {
                        output.write(event);
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    logger.ERROR(e);
                }
            }
            logger.INFO("output worker %s quited", output.id());
        }

        public TQueue<List<TEvent>> getQueue() {
            return queue;
        }

        public synchronized void startup() throws Exception {
            if (!running) {
                running = true;
                output.start();
                super.start();
            } else {
                throw new IllegalStateException("worker is running");
            }
        }

        public void shutdown() {
            if (running) {
                running = false;
                output.stop();
                output.release();
            }
        }

        @Override
        public synchronized void start() {
            throw new UnsupportedOperationException("invoke startup instead of start");
        }
    }

    class OutputWorkerConnector extends Thread {

        private          TQueue<List<TEvent>> queue;
        private volatile List<OutputWorker>   workers;
        private volatile boolean              running;

        public OutputWorkerConnector(TQueue<List<TEvent>> queue) {
            this.queue = queue;
            setDaemon(true);
        }

        public void connect(List<OutputWorker> workers) {
            this.workers = workers;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    List<TEvent> events = queue.poll(1, TimeUnit.SECONDS);
                    if (events == null || events.isEmpty()) {
                        continue;
                    }
                    for (OutputWorker worker : workers) {
                        worker.getQueue().put(events, events.size());
                    }
                } catch (Exception e) {
                    logger.ERROR(e);
                    break;
                }
            }
        }

        public synchronized void startup() {
            if (!running) {
                this.running = true;
                super.start();
            } else {
                throw new IllegalStateException("connector is running");
            }
        }

        public synchronized void shutdown() {
            if (running) {
                this.running = false;
                for (OutputWorker worker : workers) {
                    worker.shutdown();
                }
            }
        }

        @Override
        public synchronized void start() {
            throw new UnsupportedOperationException("invoke startup instead of start");
        }
    }
}
