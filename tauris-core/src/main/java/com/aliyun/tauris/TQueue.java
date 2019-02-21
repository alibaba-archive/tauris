package com.aliyun.tauris;


import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 16/12/8.
 */
public interface TQueue<T> {

    String getName();

    /**
     * 相当于put(event, 1);
     *
     * @throws InterruptedException
     */
//    default void put(T event) throws InterruptedException {
//        put(event, 1);
//    }

    /**
     * @param e
     * @param elementCount 如果e是集合类型,elementCount是集合的size; 否则是1
     * @throws InterruptedException
     */
    void put(T e, int elementCount) throws InterruptedException;

    /**
     * 相当于 offer(event, millis, 1);
     *
     * @param e
     * @param millis 单位毫秒,等待时间
     * @return
     * @throws InterruptedException
     */
//    default boolean offer(T e, long millis) throws InterruptedException {
//        return offer(e, millis, 1);
//    }

    /**
     * 向队列中添加一组item, 如果队列满则等待
     *
     * @param e
     * @param millis       单位毫秒,等待时间
     * @param elementCount 如果e是集合类型,elementCount是集合的size; 否则是1
     * @param elementCount item的大小
     */
    boolean offer(T e, long millis, int elementCount) throws InterruptedException;

    T take() throws InterruptedException;

    T poll(long timeout, TimeUnit unit) throws InterruptedException;

    boolean isEmpty();

    /**
     * 队列的size
     *
     * @return
     */
    long size();

    /**
     * 队列中item的数量
     *
     * @return
     */
    long getElementCount();

    void clear();
}
