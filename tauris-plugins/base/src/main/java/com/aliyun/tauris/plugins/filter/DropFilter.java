package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;

/**
 * Created by ZhangLei on 16/12/11.
 */
public class DropFilter extends BaseTFilter {

    public void init() {
        this.discard = true;
    }

    @Override
    public boolean doFilter(TEvent event) {
        return false;
    }
}
