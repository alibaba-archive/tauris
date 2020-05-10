package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.filter.iploc.IPInfo;
import com.aliyun.tauris.plugins.filter.iploc.TIPLocator;
import com.aliyun.tauris.TLogger;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 17/1/8.
 */
@Name("iploc")
public class IPLocFilter extends BaseTFilter {

    private TLogger logger;

    @Required
    String source;

    @Required
    String target;

    @Required
    TIPLocator locator;

    int cacheSize = 0;

    int cacheExpiredSeconds = 0;

    private LoadingCache<String, Optional<Map>> _cache = null;

    public void init() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        if (cacheSize > 0) {
            CacheBuilder<Object, Object> b = CacheBuilder.newBuilder().maximumSize(cacheSize);
            if (cacheExpiredSeconds > 0) {
                b.expireAfterAccess(cacheExpiredSeconds, TimeUnit.SECONDS);
            }
            _cache = b.build(new CacheLoader<String, Optional<Map>>() {
                public Optional<Map> load(String ip) {
                    IPInfo info = locator.locate(ip);
                    if (info != null) {
                        return Optional.of(info.toMap());
                    }
                    return Optional.empty();
                }
            });
        }
        locator.prepare();
    }

    @Override
    public boolean doFilter(TEvent event) {
        Object key = event.get(source);
        if (key != null) {
            Map tgt = null;
            if (_cache == null) {
                IPInfo info = locator.locate(key.toString());
                if (info != null) {
                    tgt = info.toMap();
                }
            } else {
                try {
                    Optional<Map> opt = _cache.get(key.toString());
                    tgt = opt.isPresent() ? opt.get() : null;
                } catch (ExecutionException e) {
                    logger.ERROR("load iploc from cache exception", e);
                    return true;
                } catch (CacheLoader.InvalidCacheLoadException e) {
                    return true;
                }
            }
            if (tgt != null && !tgt.isEmpty()) {
                event.set(target, tgt);
            }
        }
        return true;
    }
}
