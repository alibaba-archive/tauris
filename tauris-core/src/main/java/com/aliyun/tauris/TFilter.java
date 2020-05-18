package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type("filter")
public interface TFilter extends TPlugin {

    default boolean test(TEvent event) { return true; }

    default void prepare() throws TPluginInitException {}

    TEvent filter(TEvent event);
}
