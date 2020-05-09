package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class SplitFilter extends BaseTFilter {

    String source = "@source";

    @Required
    String separator = "\\|";

    String target;

    @Deprecated
    int count = 0;

    @Deprecated
    String[] names;

    public void init() {
        if (count == 0 && names != null) {
            count = names.length;
        }
    }

    @Override
    protected boolean doFilter(TEvent e) {
        String text = (String) e.get(source);
        if (text == null) {
            return false;
        }
        String[] vs = text.split(separator);
        if (count > 0) {
            if (vs.length < count) {
                return true;
            }
            for (int i = 0; i < names.length; i++) {
                e.set(names[i], vs[i]);
            }
        }
        if (target != null){
            e.set(target, vs);
        }
        return true;
    }
}
