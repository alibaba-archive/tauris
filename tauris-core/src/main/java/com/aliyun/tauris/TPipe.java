package com.aliyun.tauris;

/**
 * Class TPipe
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public interface TPipe<T> {

    String getName();

    void put(T e) throws InterruptedException;


    default void open() throws Exception {
    }

    default void close() {
    }

    default void join(TPipe<T> pipe) {

    }
}
