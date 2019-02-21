package com.aliyun.tauris.plugins.output.stats;

import com.aliyun.tauris.TEvent;

/**
 * Class LabelField
 *
 * @author yundun-waf-dev
 * @date 2018-07-10
 */
public class LabelField {

    private String label;
    private String field;

    public LabelField(String expr) {
        if (expr.contains(":")) {
            String[] ps = expr.split(":");
            this.label = ps[0];
            this.field = ps[1];
        } else {
            this.label = expr;
            this.field = expr;
        }
    }

    public String getLabel() {
        return label;
    }

    public String getField() {
        return field;
    }

    public String labelOf(TEvent event) {
        Object o = event.get(field);
        return o == null ? null : o.toString();
    }
}
