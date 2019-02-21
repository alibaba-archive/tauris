package com.aliyun.tauris.plugins.filter.mutate;

import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 16/12/14.
 */
public class LongConverter extends AbstractConverter {

    public LongConverter() {
        super(Long.class, Pattern.compile("^(\\-)?\\d+$"));
    }

    @Override
    protected Object convert(Object value) {
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        return super.convert(value);
    }
}
