package com.aliyun.tauris.pipeline;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPipe;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.metrics.Gauge;
import com.aliyun.tauris.TLogger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Class AbstractPipe
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public abstract class AbstractPipe implements TPipe<TEvent> {

    protected static final Gauge   TAURIS_PIPE_SIZE     = Gauge.build().name("tauris_pipe_size").labelNames("name").help("pipe 's size").create().register();
    protected static final Counter TAURIS_PIPE_RECEIVED = Counter.build().name("tauris_pipe_receives").labelNames("name").help("pipe received event count").create().register();

    protected TLogger logger;

    protected String                name;
    protected BlockingQueue<TEvent> queue;
    protected int queueCapacity = 0;
    protected volatile boolean opened;
    protected long pollInterval   = 100;
    protected int  threadPriority = Thread.NORM_PRIORITY;

    public AbstractPipe(String name) {
        this.name = name;
    }

    public AbstractPipe(String name, int queueCapacity) {
        this.name = name;
        this.queueCapacity = queueCapacity;
        this.logger = TLogger.getLogger(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void put(TEvent event) throws InterruptedException {
        if (this.queue != null) {
            this.queue.put(event);
            TAURIS_PIPE_SIZE.labels(name).set(this.queue.size());
        } else {
            write(event);
        }
        TAURIS_PIPE_RECEIVED.labels(name).inc();
    }

    @Override
    public void open() throws Exception {
        if (queueCapacity > 0) {
            this.queue = new ArrayBlockingQueue<>(queueCapacity);
            Thread t = new Thread(() -> {
                this.opened = true;
                try {
                    while (opened || !queue.isEmpty()) {
                        try {
                            TEvent event = queue.poll(pollInterval, TimeUnit.MILLISECONDS);
                            if (event != null) {
                                write(event);
                                TAURIS_PIPE_SIZE.labels(name).set(this.queue.size());
                            }
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    logger.INFO("pipe %s closed", getName());
                } catch (Exception e) {
                    logger.EXCEPTION(e);
                }
            });
            t.setDaemon(true);
            t.setPriority(threadPriority);
            t.start();
        }
    }

    protected abstract void write(TEvent event) throws InterruptedException;

    @Override
    public void close() {
        this.opened = false;
    }
}
