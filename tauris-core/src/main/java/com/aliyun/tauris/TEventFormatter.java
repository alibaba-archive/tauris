package com.aliyun.tauris;

/**
 * Class TEventFormatter
 *
 * @author yundun-waf-dev
 * @date 2018-06-22
 */
public interface TEventFormatter extends TPlugin {

    void init() throws TPluginInitException;

    String format(TEvent event);

}
