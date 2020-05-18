package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.utils.EventFormatter;

import javax.annotation.PreDestroy;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class MetricFilter extends BaseTFilter {

    @Required
    String name;

    @Required
    String help;

    String[] labelNames;

    EventFormatter[] labels;

    String valueField;

    private Counter counter;

    public void init() throws TPluginInitException {
        Counter.Builder builder = Counter.build().name(name);
        if (labelNames != null && labelNames.length > 0) {
            counter = builder.labelNames(labelNames).help(help).create().register();
        } else {
            counter = builder.help(help).create().register();
        }
    }

    @Override
    public boolean doFilter(TEvent event) {
        if (labelNames != null && labelNames.length > 0) {
            String[] labels = new String[labelNames.length];
            for (int i = 0; i < this.labels.length; i++) {
                String label = this.labels[i].format(event);
                if (label == null) {
                    return true;
                }
                labels[i++] = label;
            }
            if (valueField != null) {
                try {
                    Number val = (Number) event.get(valueField);
                    if (val != null) {
                        counter.labels(labels).inc(val.doubleValue());
                    }

                } catch (ClassCastException e) {
                    throw new ClassCastException(String.format("%s cannot be cast to number", valueField));
                }
            } else {
                counter.labels(labels).inc();
            }
        } else {
            if (valueField != null) {
                try {
                    Number val = (Number) event.get(valueField);
                    if (val != null) {
                        counter.inc(val.doubleValue());
                    }

                } catch (ClassCastException e) {
                    throw new ClassCastException(String.format("%s cannot be cast to number", valueField));
                }
            } else {
                counter.inc();
            }
        }
        return true;
    }

    @PreDestroy
    public void release() {
        counter.unregister();
    }
}
