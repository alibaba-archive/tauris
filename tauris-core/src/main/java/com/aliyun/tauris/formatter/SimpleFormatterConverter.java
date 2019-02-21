package com.aliyun.tauris.formatter;

import com.aliyun.tauris.utils.TConverter;

/**
 * Created by ZhangLei on 17/1/7.
 */
public class SimpleFormatterConverter extends TConverter {

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("cannot convert " + value.getClass() + " to simple event formatter");
        }
        String strVal = (String)value;
        SimpleFormatter formatter = SimpleFormatter.build(strVal);
        return (T)formatter;
    }

    @Override
    public Class<?> getType() {
        return SimpleFormatter.class;
    }
}
