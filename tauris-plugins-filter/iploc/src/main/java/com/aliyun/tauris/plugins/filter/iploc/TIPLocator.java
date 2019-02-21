package com.aliyun.tauris.plugins.filter.iploc;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TPluginInitException;

/**
 * Created by ZhangLei on 17/6/1.
 */
public interface TIPLocator extends TPlugin {

    /**
     * 执行update前调用
     */
    default void prepare() throws TPluginInitException {}

    IPInfo locate(String ip);

}
