package com.aliyun.tauris;

/**
 * Created by ZhangLei on 16/12/8.
 */
public interface TFilter extends TPlugin {

    default boolean test(TEvent event) { return true; }

    default void prepare() throws TPluginInitException {}

    TEvent filter(TEvent event);
}
