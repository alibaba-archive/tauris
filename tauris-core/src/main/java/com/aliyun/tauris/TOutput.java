package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Type("output")
public interface TOutput extends TPlugin {

    default void start() throws Exception {}

    default boolean write(TEvent event) {
        return false;
    };

    default void stop() {
        this.release();
    };

}
