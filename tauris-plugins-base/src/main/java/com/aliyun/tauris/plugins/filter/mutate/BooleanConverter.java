package com.aliyun.tauris.plugins.filter.mutate;

/**
 * Created by ZhangLei on 16/12/14.
 */
public class BooleanConverter extends AbstractConverter {

    public BooleanConverter() {
        super(Boolean.class);
    }

    @Override
    protected Object convert(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return value;
        if (value instanceof String) {
            String s = (String)value;
            return s.equals("on") || s.equals("true");
        }
        if (value instanceof Number) {
            Number s = (Number)value;
            return s.intValue() != 0;
        }
        return super.convert(value);
    }
}
