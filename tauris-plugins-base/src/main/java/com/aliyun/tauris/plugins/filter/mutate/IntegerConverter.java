package com.aliyun.tauris.plugins.filter.mutate;

import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 16/12/14.
 */
public class IntegerConverter extends AbstractConverter {

    public IntegerConverter() {
        super(Integer.class, Pattern.compile("^(\\-)?\\d+$"));
    }

    @Override
    protected Object convert(Object value) {
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        return super.convert(value);
    }
}
