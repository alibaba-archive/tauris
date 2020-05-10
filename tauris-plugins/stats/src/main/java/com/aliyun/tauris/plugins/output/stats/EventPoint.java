package com.aliyun.tauris.plugins.output.stats;

/**
 * Class EventPoint
 *
 * @author yundun-waf-dev
 * @date 2018-07-10
 */
public class EventPoint {

    private Labels labels;

    private Double[] values;

    public EventPoint(Labels labels, Double[] values) {
        this.labels = labels;
        this.values = values;
    }

    public Labels getLabels() {
        return labels;
    }

    public Double[] getValues() {
        return values;
    }
}