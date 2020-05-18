package com.aliyun.tauris.plugins.filter.iploc;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Type;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type("iplocator")
public interface TIPLocator extends TPlugin {

    /**
     * 执行update前调用
     */
    default void prepare() throws TPluginInitException {}

    IPInfo locate(String ip);

}
