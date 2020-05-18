package com.aliyun.tauris.plugins.filter.mutate;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class FloatConverter extends AbstractConverter {

    public FloatConverter() {
        super(Float.class, Pattern.compile("^(-?\\d+)(\\.\\d+)?$"));
    }

    @Override
    protected Optional<Object> convert(Object value) {
        if (value instanceof Number) {
            return Optional.of(((Number) value).floatValue());
        }
        return super.convert(value);
    }
}
