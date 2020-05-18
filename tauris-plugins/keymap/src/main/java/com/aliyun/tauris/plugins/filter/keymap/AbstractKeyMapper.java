package com.aliyun.tauris.plugins.filter.keymap;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */

import com.aliyun.tauris.TPluginInitException;

/**
 * @author Ray Chaung<rockis@gmail.com>
 *
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
