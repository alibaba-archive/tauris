package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.plugins.filter.cache.BackendPools;
import com.aliyun.tauris.plugins.filter.cache.TCacheAction;
import com.aliyun.tauris.plugins.filter.cache.TCacheBackend;
import com.aliyun.tauris.utils.EventFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("cache")
public class CacheFilter extends BaseTFilter {

    private static Logger logger = LoggerFactory.getLogger(CacheFilter.class);

    @Required
    TCacheAction action;

    String backendName = "default";

    TCacheBackend backend;

    String target;

    @Required
    EventFormatter key;

    EventFormatter value;

    public void init() throws TPluginInitException {
        if (action == TCacheAction.set && value == null) {
            throw new TPluginInitException("value is required when action is set");
        }
        if (action == TCacheAction.get && target == null) {
            throw new TPluginInitException("target is required when action is get");
        }
        if (backend == null) {
            backend = BackendPools.getBackend(backendName);
            if (backend == null) {
                throw new TPluginInitException("there no cache backend `" + backendName + "` configured");
            }
        } else {
            if (BackendPools.getBackend(backendName) != null) {
                throw new TPluginInitException("cache backend `" + backendName + "` already configuired");
            }
            BackendPools.setBackend(backendName, backend);
        }
    }

    @Override
    public void prepare() throws TPluginInitException {
    }

    @Override
    public boolean doFilter(TEvent event) {
        String key = this.key.format(event);
        Object val = null;
        switch (action) {
            case set:
                val = this.value.format(event);
                backend.set(key, val);
                break;
            case get:
                val = backend.get(key);
                event.set(target, val);
                break;
            case setnx:
                val = this.value.format(event);
                boolean ret = backend.setnx(key, val);
                if (target != null) {
                    event.set(target, ret);
                }
                break;
            case delete:
                backend.delete(key);
                break;
        }
        return true;
    }

    @Override
    public void release() {
        this.backend.destroy();
    }
}
