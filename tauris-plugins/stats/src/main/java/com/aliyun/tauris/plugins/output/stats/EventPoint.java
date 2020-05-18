package com.aliyun.tauris.plugins.output.stats;

/**
 * Class EventPoint
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
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