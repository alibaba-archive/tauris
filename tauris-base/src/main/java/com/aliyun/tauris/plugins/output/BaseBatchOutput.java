package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Gauge;
import com.aliyun.tauris.TLogger;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ZhangLei on 16/12/8.
 */
public abstract class BaseBatchOutput extends BaseTOutput {

    private static Gauge WAITING_TASK_COUNT = Gauge.build().name("input_batch_waiting_tasks").labelNames("id").help("waiting task count").create().register();
    private static Gauge IDLE_WORKER_COUNT  = Gauge.build().name("input_batch_idle_workers").labelNames("id").help("idle worker count").create().register();

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
    protected int maxTaskQueueSize = 10;

    private TLogger        logger;
    private BatchWriteTask task;
    private Thread         forceFlushThread;
    private Thread         executeThread;
    private long           lastFlushTime;

    private BlockingQueue<BatchWriteTask> taskQueue;
    private BlockingQueue<TaskWorker>     workerQueue;

    public BaseBatchOutput() {
        logger = TLogger.getLogger(this);
    }

    public void init() throws TPluginInitException {
        if (maxTaskQueueSize > 0) {
            taskQueue = new ArrayBlockingQueue<>(maxTaskQueueSize);
        } else {
            taskQueue = new LinkedBlockingDeque<>();
        }
        workerQueue = new ArrayBlockingQueue<>(writeThreadCount);
        for (int i = 0; i < writeThreadCount; i++) {
            workerQueue.add(new TaskWorker());
        }
        IDLE_WORKER_COUNT.labels(id()).set(writeThreadCount);

        executeThread = new Thread(() -> {
            while (true) {
                try {
                    BatchWriteTask task = taskQueue.take();
                    TaskWorker worker = workerQueue.take();
                    worker.setTask(task);
                    WAITING_TASK_COUNT.labels(id).dec();
                    IDLE_WORKER_COUNT.labels(id).dec();
                    new Thread(worker).start();
                } catch (InterruptedException e) {
                    logger.WARN("task queue has been interrupted");
                    break;
                }
            }
        });
        executeThread.start();

        forceFlushThread = new Thread(() -> {
            while (true) {
                synchronized (BaseBatchOutput.this) {
                    if (task != null && (!task.isEmpty() && System.currentTimeMillis() - lastFlushTime > linger)) {
                        flush(false);
                    }
                }
                try {
                    Thread.sleep(linger);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        forceFlushThread.start();
    }

    @Override
    public synchronized void doWrite(TEvent event) {
        try {
            if (task == null) {
                task = newTask();
            }
            int ec = task.append(event);
            if (ec >= batchSize) {
                flush(writeThreadCount > 1);
            }
        } catch (Exception e) {
            logger.EXCEPTION(e);
        }
    }

    private synchronized void flush(boolean async) {
        if (task != null && !task.isEmpty()) {
            try {
                if (async) {
                    batchWriteAsync(task);
                } else {
                    batchWrite(task);
                }
                task = newTask();
            } catch (Exception e) {
                logger.EXCEPTION(e);
            }
            lastFlushTime = System.currentTimeMillis();
        }
    }

    private void batchWriteAsync(BatchWriteTask task) {
        try {
            taskQueue.put(task);
            WAITING_TASK_COUNT.labels(id).inc();
        } catch (InterruptedException e) {
            logger.ERROR(e);
        }
    }

    private void batchWrite(BatchWriteTask task) {
        task.execute();
    }


    protected abstract BatchWriteTask newTask() throws Exception;

    @Override
    public void stop() {
        super.stop();
        forceFlushThread.interrupt();
        flush(false);
        executeThread.interrupt();
    }

    protected abstract class BatchWriteTask {

        private AtomicInteger elementCount = new AtomicInteger();

        public int elementCount() {
            return elementCount.get();
        }

        protected boolean isEmpty() {
            return elementCount.get() == 0;
        }

        protected final int append(TEvent event) throws Exception {
            try {
                accept(event);
                return elementCount.incrementAndGet();
            } catch (EncodeException e) {
                logger.ERROR("encode error", e);
                return elementCount.get();
            }
        }

        protected abstract void accept(TEvent event) throws EncodeException, IOException;

        protected abstract void execute();
    }

    private class TaskWorker implements Runnable {

        private BatchWriteTask task;

        public void setTask(BatchWriteTask task) {
            this.task = task;
        }

        public void run() {
            try {
                task.execute();
            } catch (Exception e) {
                logger.EXCEPTION(e);
            } finally {
                this.task = null;
                workerQueue.add(this);
                IDLE_WORKER_COUNT.labels(id).inc();
            }
        }
    }
}
