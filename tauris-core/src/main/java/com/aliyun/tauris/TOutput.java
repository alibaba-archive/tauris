package com.aliyun.tauris;

/**
 * Created by ZhangLei on 16/12/8.
 */
public interface TOutput extends TPlugin {

    default void start() throws Exception {}

    /**
     *
     * @param event
     */
    default boolean check(TEvent event) { return true; };

    default boolean write(TEvent event) {
        return false;
    };

    default void stop() {
        this.release();
    };

}
