package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.filter.mutate.*;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class ConverterFilter extends BaseTFilter {

    @Required
    TFieldConverter[] convert;

    public void init() {
    }

    @Override
    public boolean doFilter(TEvent e) {
        for (TFieldConverter converter : convert) {
            converter.convert(e);
        }
        return true;
    }
}
