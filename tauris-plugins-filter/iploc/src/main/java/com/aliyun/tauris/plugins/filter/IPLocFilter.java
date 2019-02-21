package com.aliyun.tauris.plugins.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.filter.iploc.IPInfo;
import com.aliyun.tauris.plugins.filter.iploc.TIPLocator;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Created by ZhangLei on 17/1/8.
 */
@Name("iploc")
public class IPLocFilter extends BaseTFilter {

    private static Logger LOG = LoggerFactory.getLogger(IPLocFilter.class);

    @Required
    String source;

    @Required
    String target;

    @Required
    TIPLocator locator;

    int cacheSize = 0;

    private LoadingCache<String, Optional<JSONObject>> _cache = null;

    public void init() throws TPluginInitException {
        if (cacheSize > 0) {
            _cache = CacheBuilder.newBuilder().maximumSize(cacheSize)
                    .build(new CacheLoader<String, Optional<JSONObject>>() {
                        public Optional<JSONObject> load(String ip) {
                            IPInfo info = locator.locate(ip);
                            if (info != null) {
                                return Optional.of((JSONObject) JSON.toJSON(info));
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
            JSONObject tgt = null;
            if (_cache == null) {
                IPInfo info = locator.locate(key.toString());
                if (info != null) {
                    tgt = (JSONObject) JSON.toJSON(info);
                }
            } else {
                try {
                    Optional<JSONObject> opt = _cache.get(key.toString());
                    tgt = opt.isPresent() ? opt.get() : null;
                } catch (ExecutionException e) {
                    LOG.error("load iploc from cache exception", e);
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
