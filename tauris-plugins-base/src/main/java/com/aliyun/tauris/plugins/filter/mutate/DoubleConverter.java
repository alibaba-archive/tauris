package com.aliyun.tauris.plugins.filter.mutate;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 16/12/14.
 */
public class DoubleConverter extends AbstractConverter {

    public DoubleConverter() {
        super(Double.class, Pattern.compile("^(-?\\d+)(\\.\\d+)?$"));
    }

    @Override
    protected Optional<Object> convert(Object value) {
        if (value instanceof Number) {
            return Optional.of(((Number) value).doubleValue());
        }
        return super.convert(value);
    }
}
