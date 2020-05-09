package com.aliyun.tauris;

/**
 * Class TPipe
 *
 * @author yundun-waf-dev
 * @date 2019-04-15
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
