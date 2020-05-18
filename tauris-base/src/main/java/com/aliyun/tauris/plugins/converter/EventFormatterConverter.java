package com.aliyun.tauris.plugins.converter;

import com.aliyun.tauris.utils.EventFormatter;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class EventFormatterConverter extends AbstractConverter {

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("cannot convert " + value.getClass() + " to simple event formatter");
        }
        String strVal = (String)value;
        EventFormatter formatter = EventFormatter.build(strVal);
        return (T)formatter;
    }

    @Override
    public Class<?> getType() {
        return EventFormatter.class;
    }
}
