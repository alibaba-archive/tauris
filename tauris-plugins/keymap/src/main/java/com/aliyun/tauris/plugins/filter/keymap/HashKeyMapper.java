package com.aliyun.tauris.plugins.filter.keymap;

import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;

import java.util.Collections;
import java.util.Map;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("hash")
public class HashKeyMapper extends AbstractKeyMapper {

    @Required
    Map<String, Object> values;

    public void init() throws TPluginInitException {
        values = Collections.unmodifiableMap(values);
    }

    @Override
    public Object getValue(String key) {
        Object o = values.get(key);
        if (o == null && defaultValue != null) {
            return defaultValue;
        }
        return o;
    }
}
