package com.aliyun.tauris;

import java.util.Set;

/**
 * Created by ZhangLei on 17/5/23.
 */
public interface TPluginFactory {

    default TPlugin newInstance(String pluginName) {
        return newInstance(pluginName, "default");
    };

    TPlugin newInstance(String pluginName, String minorName);

    Set<Class<? extends TPlugin>> getPluginClasses();
}
