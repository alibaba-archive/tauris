package com.aliyun.tauris.plugins.filter.keymap;

/**
 * Created by ZhangLei on 2018/6/4.
 */

import com.aliyun.tauris.TPluginInitException;

/**
 * @author yundun-waf-dev
 * @date 2018-06-04
 */
public abstract class AbstractKeyMapper implements TKeyMapper {

    protected String defaultValue;

    @Override
    public Object get(String key) {
        Object o = getValue(key);
        if (o == null && defaultValue != null) {
            return defaultValue;
        }
        return o;
    }

    public void prepare() throws TPluginInitException  {

    }

    protected abstract Object getValue(String key);
}
