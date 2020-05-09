package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Type("filter")
public interface TFilter extends TPlugin {

    default boolean test(TEvent event) { return true; }

    default void prepare() throws TPluginInitException {}

    TEvent filter(TEvent event);
}
