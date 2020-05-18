package com.aliyun.tauris.plugins.filter.mutate;

import java.util.Optional;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class BooleanConverter extends AbstractConverter {

    public BooleanConverter() {
        super(Boolean.class);
    }

    @Override
    protected Optional<Object> convert(Object value) {
        if (value == null) Optional.of(false);
        if (value instanceof Boolean) return Optional.of(value);
        if (value instanceof String) {
            String s = (String)value;
            return Optional.of(s.equals("on") || s.equals("true"));
        }
        if (value instanceof Number) {
            Number s = (Number)value;
            return Optional.of(s.intValue() != 0);
        }
        return super.convert(value);
    }
}
