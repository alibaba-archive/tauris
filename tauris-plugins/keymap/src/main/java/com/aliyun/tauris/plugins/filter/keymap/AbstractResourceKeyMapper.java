package com.aliyun.tauris.plugins.filter.keymap;

import com.aliyun.tauris.PluginTools;
import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;

import javax.annotation.PreDestroy;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class AbstractResourceKeyMapper extends AbstractKeyMapper {

    protected ConcurrentHashMap<String, Object> mapping = new ConcurrentHashMap<>();

    protected final ReentrantLock lock = new ReentrantLock();

    @Required
    protected TResource resource;

    protected Charset charset = Charset.defaultCharset();

    @Override
    public void prepare() throws TPluginInitException {
        try {
            this.watch();
        } catch (Exception e) {
            throw new TPluginInitException("watch resource " + resource.getURI().toString() + " failed", e);
        }
    }

    public void watch() throws Exception {
        update(new String(resource.fetch(), charset));
        resource.watch((s) -> {
            if (s == null) {
                return;
            }
            update(new String(s, charset));
        });
    }

    protected abstract void update(String text);

    protected void update(Map<String, Object> v) {
        lock.lock();
        mapping.clear();
        mapping.putAll(v);
        lock.unlock();
    }

    @Override
    public Object getValue(String key) {
        Object val;
        lock.lock();
        val = mapping.get(key);
        lock.unlock();
        return resolveValue(val);
    }

    public abstract Object resolveValue(Object value);

    @Override
    public void release() {
        mapping.clear();
        resource.release();
    }
}
