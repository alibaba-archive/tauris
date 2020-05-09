package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TLogger;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.filter.load.TLoader;

import java.util.Map;

/**
 * Created by ZhangLei on 19/6/19.
 */
@Name("load")
public class LoadFilter extends BaseTFilter {

    TLogger logger;

    @Required
    TResource resource;

    @Required
    TLoader format;

    @Override
    public void prepare() throws TPluginInitException {
        logger = TLogger.getLogger(this);
        try {
            format.unmarshal(resource.fetchText());
        } catch (Exception e) {
            throw new TPluginInitException("resource load failed", e);
        }
        resource.watch((s) -> {
            if (s == null) {
                return;
            }
            try {
                format.unmarshal(resource.fetchText());
            } catch (Exception e) {
                logger.ERROR("resource unmarshal failed", e);
            }
        });
    }

    @Override
    public boolean doFilter(TEvent event) {
        Map<String, Object> ext = format.get();
        for (Map.Entry<String, Object> e: ext.entrySet()) {
            event.set(e.getKey(), e.getValue());
        }
        return true;
    }
}
