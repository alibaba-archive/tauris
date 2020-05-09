package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.formatter.TEventFormatter;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("strformat")
public class StrFormatFilter extends BaseTFilter {

    @Required
    String target;

    @Required
    TEventFormatter format;

    protected boolean doFilter(TEvent e) {
        if (e == null) {
            return false;
        }
        e.set(target, format.format(e));
        return true;
    }
}
