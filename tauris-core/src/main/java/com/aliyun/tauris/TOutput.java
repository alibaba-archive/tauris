package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

/**
 * @author Ray Chaung<rockis@gmail.com>
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
