package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.filter.keymap.TKeyMapper;

import javax.annotation.PreDestroy;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("keymap")
public class KeyMapFilter extends BaseTFilter {

    @Required
    String source;

    @Required
    String target;

    @Required
    TKeyMapper mapper;

    @Override
    public void prepare() throws TPluginInitException {
        mapper.prepare();
    }

    @Override
    public boolean doFilter(TEvent event) {
        Object key = event.get(source);
        if (key != null) {
            Object tgt = mapper.get(key.toString());
            event.set(target, tgt);
        }
        return true;
    }
}
