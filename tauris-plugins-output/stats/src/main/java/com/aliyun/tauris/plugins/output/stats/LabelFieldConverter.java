package com.aliyun.tauris.plugins.output.stats;

import com.aliyun.tauris.utils.TConverter;

/**
 * Created by ZhangLei on 17/1/7.
 */
public class LabelFieldConverter extends TConverter {

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("cannot convert " + value.getClass() + " to LabelField");
        }
        String strVal = (String)value;
        return (T) new LabelField(strVal);
    }

    @Override
    public Class<?> getType() {
        return LabelField.class;
    }
}
