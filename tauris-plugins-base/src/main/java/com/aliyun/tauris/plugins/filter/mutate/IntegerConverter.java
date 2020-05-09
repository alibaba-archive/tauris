package com.aliyun.tauris.plugins.filter.mutate;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 16/12/14.
 */
public class IntegerConverter extends AbstractConverter {

    public IntegerConverter() {
        super(Integer.class, Pattern.compile("^(\\-)?\\d+$"));
    }

    @Override
    protected Optional<Object> convert(Object value) {
        if (value instanceof Number) {
            return Optional.of(((Number)value).intValue());
        }
        return super.convert(value);
    }
}
