package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 16/12/11.
 */
@Name("timestamp")
public class TimestampFilter extends BaseTFilter {

    @Required
    String target;

    TimeUnit timeunit;

    @Override
    public boolean doFilter(TEvent event) {
        long now = System.currentTimeMillis();
        if (timeunit != null) {
            now = timeunit.convert(now, TimeUnit.MILLISECONDS);
        }
        event.set(target, now);
        return true;
    }
}
