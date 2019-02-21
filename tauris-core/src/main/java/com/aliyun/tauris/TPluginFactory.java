package com.aliyun.tauris;

import java.util.Set;

/**
 * Created by ZhangLei on 17/5/23.
 */
public interface TPluginFactory {

    TPlugin newInstance(String pluginName);

    Set<Class<? extends TPlugin>> getPluginClasses();
}
