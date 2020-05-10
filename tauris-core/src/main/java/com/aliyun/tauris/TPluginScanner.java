package com.aliyun.tauris;

import java.util.Set;

/**
 * Class TPluginScanner
 *
 * @author yundun-waf-dev
 * @date 2019-03-21
 */
public interface TPluginScanner {

    Set<Class<? extends TPlugin>> scanPluginTypes();

    Set<Class<? extends TPlugin>> scanPluginClasses(Class<? extends TPlugin> pluginType);

}
