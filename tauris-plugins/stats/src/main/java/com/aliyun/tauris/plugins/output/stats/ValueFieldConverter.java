package com.aliyun.tauris.plugins.output.stats;


import com.aliyun.tauris.plugins.converter.AbstractConverter;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class ValueFieldConverter extends AbstractConverter {

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
