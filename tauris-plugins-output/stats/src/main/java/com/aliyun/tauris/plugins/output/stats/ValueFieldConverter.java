package com.aliyun.tauris.plugins.output.stats;

import com.aliyun.tauris.utils.TConverter;

/**
 * Created by ZhangLei on 17/1/7.
 */
public class ValueFieldConverter extends TConverter {

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("cannot convert " + value.getClass() + " to ValueField");
        }
        String strVal = (String)value;
        return (T) new ValueField(strVal);
    }

    @Override
    public Class<?> getType() {
        return ValueField.class;
    }
}
