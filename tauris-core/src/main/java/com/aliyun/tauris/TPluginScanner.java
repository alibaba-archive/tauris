package com.aliyun.tauris;

import java.util.Set;

/**
 * Class TPluginScanner
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public interface TPluginScanner {

    Set<Class<? extends TPlugin>> scanPluginTypes();

    Set<Class<? extends TPlugin>> scanPluginClasses(Class<? extends TPlugin> pluginType);

}
