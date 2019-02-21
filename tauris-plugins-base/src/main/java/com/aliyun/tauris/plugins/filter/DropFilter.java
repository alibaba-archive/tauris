package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.metric.CounterWidget;

/**
 * Created by ZhangLei on 16/12/11.
 */
public class DropFilter extends BaseTFilter {

    private CounterWidget metric = null;

    public void init() {
        this.discard = true;
    }

    @Override
    public boolean doFilter(TEvent event) {
        if (metric != null) {
            metric.inc(event);
        }
        return false;
    }
}
