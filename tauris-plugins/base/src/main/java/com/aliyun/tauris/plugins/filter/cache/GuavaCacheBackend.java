package com.aliyun.tauris.plugins.filter.cache;

import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 2018/5/21.
 */
@Name("guava")
public class GuavaCacheBackend implements TCacheBackend {

    private Cache<String, Object> cache;

    Integer expired;

    Integer maximumSize = 1000;

    private final Object lock = new Object();

    @Override
    public void init() throws TPluginInitException {
        if (expired != null) {
            cache = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(expired, TimeUnit.SECONDS).build();
        } else {
            cache = CacheBuilder.newBuilder().maximumSize(maximumSize).build();
        }
    }

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public boolean setnx(String key, Object value) {
        synchronized (lock) {
            if (cache.getIfPresent(key) != null) {
                return false;
            }
            cache.put(key, value);
            return true;
        }
    }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    @Override
    public void destroy() {
        cache.invalidateAll();
        cache.cleanUp();
    }
}
