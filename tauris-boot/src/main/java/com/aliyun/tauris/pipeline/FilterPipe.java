package com.aliyun.tauris.pipeline;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TFilterGroup;
import com.aliyun.tauris.TPipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Class FilterPipe
 *
 * @author yundun-waf-dev
 * @date 2019-04-17
 */
public class FilterPipe extends AbstractPipe {

    private BlockingQueue<TEvent> queue;
    private List<TFilterGroup>    filters;
    private List<FilterWorker>    workers;
    private DistributePipe        next;
    private int                   workerCount;

    public FilterPipe(List<TFilterGroup> filters, int workerCount, int queueCapacity) {
        super("filter", queueCapacity);
        this.filters = filters;
        this.workerCount = workerCount;
        if (workerCount > 1) {
            queue = queueCapacity == 0 ? new SynchronousQueue<>() : new ArrayBlockingQueue<>(queueCapacity);
        }
    }

    @Override
    public void open() throws Exception {
        for (TFilterGroup fg : filters) {
            fg.prepare();
        }
        this.next.open();
        workers = new ArrayList<>(workerCount);
        if (!workers.isEmpty()) {
            for (int i = 0; i < workerCount; i++) {
                workers.add(new FilterWorker(filters, queue, next));
            }
            if (workers != null) {
                workers.forEach(Thread::start);
            }
        }
    }

    @Override
    public void put(TEvent event) throws InterruptedException {
        if (workers.isEmpty()) {
            for (TFilterGroup fg: filters) {
                TEvent e = event;
                event = fg.filter(event);
                if (event == null) {
                    e.destroy();
                    break;
                }
            }
            if (event != null) {
                write(event);
            }
        }
    }

    @Override
    public void close() {
        if (workers != null) {
            for (FilterWorker worker : workers) {
                worker.shutdown();
            }
        }
        for (TFilterGroup fg : filters) {
            fg.release();
        }
        this.next.close();
    }

    @Override
    public void join(TPipe<TEvent> pipe) {
        this.next = (DistributePipe)pipe;
    }

    @Override
    protected void write(TEvent event) throws InterruptedException {
        next.put(event);
    }
}
