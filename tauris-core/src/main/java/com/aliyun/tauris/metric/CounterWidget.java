package com.aliyun.tauris.metric;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TWidget;
import com.aliyun.tauris.formatter.SimpleFormatter;
import com.aliyun.tauris.annotations.Required;

/**
 * comp {
 *     metric => {
 *         name => 'something_counter';
 *         help => 'something helper';
 *     }
 * }
 * or
 * comp {
 *     metric => {
 *         name => 'something_counter';
 *         help => 'something helper';
 *         label_names => ['domain', 'status'];
 *         label_values => ['%{domain}', '%{status}'];
 *     }
 * }
 * Created by ZhangLei on 17/1/11.
 */
public class CounterWidget implements TWidget {

    @Required
    String name;

    @Required
    String help;

    String[] labelNames;

    SimpleFormatter[] labelValues;

    private ThreadLocal<String[]> labelValuesCache = new ThreadLocal<>();

    private Counter counter;

    public void init() {
        if (labelNames != null && labelNames.length != labelValues.length) {
            throw new IllegalArgumentException("number of labelNames is not equals to labelValues.");
        }
        Counter.Builder b = Counter.build().name(name).help(help);
        if (labelNames != null) {
            b.labelNames(labelNames);
        }
        counter = b.create().register();
    }

    public void inc(TEvent e) {
        if (labelNames == null) {
            counter.inc();
        } else {
            String[] lvs = labelValuesCache.get();
            if (lvs == null) {
                lvs = new String[labelNames.length];
                labelValuesCache.set(lvs);
            }
            for (int i = 0; i < lvs.length; i++ ){
                lvs[i] = labelValues[i].format(e);
            }
            counter.labels(lvs).inc();
        }
    }

    public void onDestroy() {
        labelValuesCache.remove();
    }
}
