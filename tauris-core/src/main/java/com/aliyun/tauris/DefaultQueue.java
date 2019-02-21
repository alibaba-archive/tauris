package com.aliyun.tauris;


import com.aliyun.tauris.metric.Counter;
import com.aliyun.tauris.metric.Gauge;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class DefaultQueue<T> implements TQueue<T> {

    private static final Gauge   TAURIS_QUEUE_SIZE     = Gauge.build().name("tauris_queue_size").labelNames("name").help("queue 's size").create().register();
    private static final Gauge   TAURIS_QUEUE_ELEMENTS = Gauge.build().name("tauris_queue_element_count").labelNames("name").help("how many element in queue").create().register();
    private static final Counter TAURIS_QUEUE_RECEIVED = Counter.build().name("tauris_queue_receives").labelNames("name").help("queue received event count").create().register();

    private String name;

    private BlockingQueue<Element> queue;

    private AtomicLong elementCount = new AtomicLong();

    public DefaultQueue(String name, int capacity) {
        this.name = name;
        if (capacity == 0) {
            this.queue = new SynchronousQueue<>();
        } else {
            this.queue = new ArrayBlockingQueue<>(capacity);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void put(T item, int elementCount) throws InterruptedException {
        queue.put(new Element(item, elementCount));
        TAURIS_QUEUE_SIZE.labels(name).set(queue.size());
        TAURIS_QUEUE_ELEMENTS.labels(name).inc(elementCount);
        TAURIS_QUEUE_RECEIVED.labels(name).inc(elementCount);
    }

    /**
     * 向队列中添加一组item, 如果队列满则等待
     *
     * @param item
     * @param millis 等待时间
     */
    @Override
    public boolean offer(T item, long millis, int elementCount) throws InterruptedException {
        boolean ret = queue.offer(new Element(item, elementCount), millis, TimeUnit.MILLISECONDS);
        TAURIS_QUEUE_SIZE.labels(name).set(queue.size());
        TAURIS_QUEUE_ELEMENTS.labels(name).inc(elementCount);
        TAURIS_QUEUE_RECEIVED.labels(name).inc(elementCount);
        return ret;
    }

    public T take() throws InterruptedException {
        Element element = queue.take();
        if (element != null) {
            TAURIS_QUEUE_SIZE.labels(name).set(queue.size());
            TAURIS_QUEUE_ELEMENTS.labels(name).dec(element.size);
            return element.item;
        }
        return null;
    }

    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        Element element = queue.poll(timeout, unit);
        if (element != null) {
            TAURIS_QUEUE_SIZE.labels(name).set(queue.size());
            TAURIS_QUEUE_ELEMENTS.labels(name).dec(element.size);
            return element.item;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public long size() {
        return queue.size();
    }

    @Override
    public long getElementCount() {
        return elementCount.get();
    }

    @Override
    public void clear() {
        this.queue.clear();
    }

    private class Element {
        private T   item;
        private int size;

        public Element(T item, int size) {
            this.item = item;
            this.size = size;
        }
    }
}
