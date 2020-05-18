package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;

/**
 * @author Ray Chaung<rockis@gmail.com>
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
