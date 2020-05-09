package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.*;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.metrics.Gauge;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ZhangLei on 16/12/8.
 */
public abstract class BaseBatchOutput extends BaseTOutput {

    private static Counter OUTPUT_BATCH_TOTAL         = Counter.build().name("output_batch_total").labelNames("id").help("batch output total").create().register();
    private static Counter OUTPUT_ERROR_BATCH_TOTAL   = Counter.build().name("output_error_batch_total").labelNames("id").help("output error batch total").create().register();
    private static Counter OUTPUT_BATCH_DISCARD_TOTAL = Counter.build().name("output_batch_discard_total").labelNames("id").help("batch output discard event total").create().register();
    private static Counter OUTPUT_RETRY_COUNTER       = Counter.build().name("output_batch_retry_total").labelNames("id").help("batch output retry count").create().register();
    private static Gauge   OUTPUT_TASKPOOL_USED       = Gauge.build().name("output_task_pool_used").labelNames("id").help("how many task in batch output task pool ").create().register();
    private static Gauge   OUTPUT_TASKQUEUE_USED      = Gauge.build().name("output_task_queue_used").labelNames("id").help("how many task in batch output task queue").create().register();

    private final static int BATCH_SIZE     = 100; //
    private final static int DEFAULT_LINGER = 10;  // unit second

    protected int batchSize = BATCH_SIZE;

    /**
     * 单位毫秒
     * 默认情况下缓冲区的消息会被立即发送到服务端，即使缓冲区的空间并没有被用完。
     * 可以将该值设置为大于0的值，这样发送者将等待一段时间后，再向服务端发送请求，以实现每次请求可以尽可能多的发送批量消息。
     * batchSize和linger是两种实现让客户端每次请求尽可能多的发送消息的机制，它们可以并存使用，并不冲突。
     */
    protected int linger = DEFAULT_LINGER * 1000; // unit millis

    /**
     * 写线程数
     */
    protected int writeThreadCount = 1;

    /**
     * 在任务队列中等待执行任务的最大数量，0表示无限制
     */
    protected int maxTaskQueueSize = 8;

    /**
     * 如果队列已满, 则丢弃新产生的任务, 默认为 false
     */
    protected boolean discardTaskIfQueueFull = false;

    /**
     * 写入失败后的重试次数
     */
    protected int retryTimes = 0;

    /**
     * 写入失败后最小重试间隔时间, 单位毫秒
     */
    protected long retryInterval = 500;

    /**
     * 写入失败后最大重试间隔时间, 单位毫秒
     */
    protected long maxRetryInterval = 8000;

    private TLogger logger;

    private BatchTask                currentTask;
    private BlockingQueue<BatchTask> taskQueue;

    private BatchTaskPool taskPool;

    private volatile boolean running;

    public BaseBatchOutput() {
        logger = TLogger.getLogger(this);
    }

    public void init() throws TPluginInitException {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxTaskQueueSize);
        poolConfig.setMaxIdle(maxTaskQueueSize);
        poolConfig.setMinIdle(2);

        taskQueue = maxTaskQueueSize == 0 ? new LinkedBlockingQueue<>() : new ArrayBlockingQueue<>(maxTaskQueueSize);
        taskPool = new BatchTaskPool(poolConfig);
    }

    @Override
    public void start() throws Exception {
        running = true;
        new Thread(() -> {
            while (running || currentTask != null) {
                try {
                    BatchTask task = null;
                    synchronized (BaseBatchOutput.this) {
                        if (currentTask != null && !currentTask.isEmpty()) {
                            task = currentTask;
                            currentTask = null;
                        }
                    }
                    if (task != null) {
                        putTaskIntoQueue(task);
                    }
                    Thread.sleep(linger);
                } catch (InterruptedException e) {
                    logger.WARN("task queue has been interrupted");
                    break;
                } catch (Exception e) {
                    logger.EXCEPTION(e);
                }
            }
        }).start();

        for (int i = 0; i < writeThreadCount; i++) {
            Thread t = new Thread(new BatchWorker(i), String.format("Thread-batch-output-%s-%d", id(), i));
            t.setDaemon(true);
            t.start();
        }
    }

    @Override
    public synchronized void doWrite(TEvent event) {
        try {
            if (currentTask == null) {
                currentTask = taskPool.borrowObject();
                currentTask.active();
                OUTPUT_TASKPOOL_USED.labels(id()).inc();
            }
            int ec = currentTask.append(event);
            if (ec >= batchSize) {
                putTaskIntoQueue(currentTask);
                currentTask = null;
            }
        } catch (TaskFullException e) {
            taskQueue.add(currentTask);
        } catch (Exception e) {
            logger.EXCEPTION(e);
        }
    }

    private void putTaskIntoQueue(BatchTask task) throws InterruptedException {
        if (discardTaskIfQueueFull) {
            if (!taskQueue.add(task)) {
                logger.WARN("queue full, %d events has been lost");
                OUTPUT_BATCH_DISCARD_TOTAL.labels(id()).inc(task.elementCount());
            }
        } else {
            taskQueue.put(task);
        }
        OUTPUT_TASKQUEUE_USED.labels(id()).inc();
    }

    protected abstract BatchTask createTask() throws Exception;

    @Override
    public void stop() {
        super.stop();
        running = false;
        while (!taskQueue.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private AtomicInteger tid = new AtomicInteger();

    protected abstract class BatchTask {

        private int id;

        private AtomicInteger elementCount = new AtomicInteger();

        public BatchTask() {
            id = tid.incrementAndGet();
        }

        public int elementCount() {
            return elementCount.get();
        }

        protected boolean isEmpty() {
            return elementCount.get() == 0;
        }

        protected final int append(TEvent event) throws TaskFullException, IOException {
            try {
                accept(event);
                return elementCount.incrementAndGet();
            } catch (EncodeException e) {
                logger.ERROR("encode error", e);
                return elementCount.get();
            }
        }

        protected abstract void accept(TEvent event) throws EncodeException, IOException;

        protected abstract boolean execute();

        protected void destroy() {
            passivate();
        }

        protected int getId() {
            return id;
        }

        protected void passivate() {
            elementCount.set(0);
            clear();
        }

        protected abstract void active();

        protected abstract void clear();
    }

    protected class BatchWorker implements Runnable {

        private int id;

        public BatchWorker(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (running || !taskQueue.isEmpty()) {
                BatchTask task = null;
                try {
                    task = taskQueue.take();

                    OUTPUT_TASKQUEUE_USED.labels(id()).dec();
                    int t = retryTimes;
                    long i = retryInterval;
                    while (t >= 0 && !task.execute() && running) {
                        OUTPUT_RETRY_COUNTER.labels(id()).inc();
                        Thread.sleep(i);
                        if (i < maxRetryInterval) {
                            i = i * 2;
                        }
                        t--;
                    }
                    if (t > 0) {
                        OUTPUT_BATCH_TOTAL.labels(id()).inc();
                    }
                } catch (InterruptedException e) {
                    logger.EXCEPTION(e);
                } catch (Exception e) {
                    logger.EXCEPTION(e);
                    if (task != null) {
                        OUTPUT_ERROR_BATCH_TOTAL.labels(id()).inc(task.elementCount());
                    }
                } finally {
                    if (task != null) {
                        try {
                            taskPool.returnObject(task);
                            OUTPUT_TASKPOOL_USED.labels(id()).dec();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private class BatchTaskFactory extends BasePooledObjectFactory<BatchTask> {

        @Override
        public BatchTask create() throws Exception {
            return createTask();
        }

        @Override
        public PooledObject<BatchTask> wrap(BatchTask task) {
            return new DefaultPooledObject<>(task);
        }

        @Override
        public void destroyObject(PooledObject<BatchTask> p) throws Exception {
            p.getObject().destroy();
        }

        @Override
        public void activateObject(PooledObject<BatchTask> p) throws Exception {
            p.getObject().active();
        }

        @Override
        public void passivateObject(PooledObject<BatchTask> p) throws Exception {
            p.getObject().passivate();
        }
    }

    private class BatchTaskPool extends GenericObjectPool<BatchTask> {
        public BatchTaskPool(GenericObjectPoolConfig config) {
            super(new BatchTaskFactory(), config);
        }
    }

    protected static class TaskFullException extends Exception {

        public TaskFullException(String message) {
            super(message);
        }
    }
}
