package com.aliyun.tauris.plugins.filter.keymap;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Type;

/**
 * Created by ZhangLei on 16/12/14.
 */
@Type("keymap.mapper")
public interface TKeyMapper extends TPlugin {

    void prepare() throws TPluginInitException;

    Object get(String key);

}
