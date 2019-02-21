package com.aliyun.tauris.plugins.filter.mutate;

import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 16/12/14.
 */
public class DoubleConverter extends AbstractConverter {

    public DoubleConverter() {
        super(Double.class, Pattern.compile("^(-?\\d+)(\\.\\d+)?$"));
    }

    @Override
    protected Object convert(Object value) {
        if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        return super.convert(value);
    }
}
