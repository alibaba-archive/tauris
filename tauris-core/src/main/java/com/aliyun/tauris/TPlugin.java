package com.aliyun.tauris;

/**
 * @author Ray Chaung<rockis@gmail.com>
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
