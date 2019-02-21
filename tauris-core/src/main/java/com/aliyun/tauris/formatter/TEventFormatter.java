package com.aliyun.tauris.formatter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TPluginInitException;

/**
 * Class TEventFormatter
 *
 * @author yundun-waf-dev
 * @date 2018-06-22
 */
public interface TEventFormatter extends TPlugin {

    void init() throws TPluginInitException ;

    String format(TEvent event);

}
