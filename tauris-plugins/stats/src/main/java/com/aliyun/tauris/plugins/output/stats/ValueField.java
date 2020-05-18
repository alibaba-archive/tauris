package com.aliyun.tauris.plugins.output.stats;

import com.aliyun.tauris.TEvent;

/**
 * Class ValueField
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class ValueField {

    private String valueName;
    private String field;

    public ValueField(String expr) {
        if (expr.contains(":")) {
            String[] ps = expr.split(":");
            this.valueName = ps[0];
            this.field = ps[1];
        } else {
            this.valueName = expr;
            this.field = expr;
        }
    }

    public String getValueName() {
        return valueName;
    }

    public String getField() {
        return field;
    }

    public Double valueOf(TEvent event) {
        Object value = event.get(field);
        if (value == null) {
            return null;
        }
        if (!(value instanceof Number)) {
            throw new IllegalArgumentException(String.format("%s's value is %s(%s), not a number", field, value, value.getClass()));
        }
        Number v = (Number) event.get(field);
        if (v != null) {
            return v.doubleValue();
        } else {
            return null;
        }
    }
}