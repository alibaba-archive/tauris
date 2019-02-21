package com.aliyun.tauris;

/**
 * Created by ZhangLei on 16/12/9.
 */
public interface TPlugin {

    default String id() {
        return null;
    }

    default void setId(String id)  {}

    /**
     * 释放资源
     */
    default void release() {}
}
