package com.aliyun.tauris.plugins.filter.load;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TPluginInitException;

import java.util.Map;

/**
 * Created by ZhangLei on 16/12/14.
 */
public interface TLoader extends TPlugin {

    void unmarshal(String text) throws TPluginInitException;

    Map<String, Object> get();
}
