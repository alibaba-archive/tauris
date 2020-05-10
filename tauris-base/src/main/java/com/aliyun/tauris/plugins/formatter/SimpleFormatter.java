package com.aliyun.tauris.plugins.formatter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TFormatter;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.utils.EventFormatter;

/**
 * Created by ZhangLei on 17/1/7.
 */
@Name("simple")
public class SimpleFormatter implements TFormatter {

    String expression;

    private EventFormatter formatter;

    public SimpleFormatter() {
    }

    public void init() throws TPluginInitException  {
        try {
            formatter = EventFormatter.build(expression);
        } catch (Exception e) {
            throw new TPluginInitException(e.getMessage());
        }
    }

    @Override
    public String format(TEvent e) {
        return formatter.format(e);
    }
}
