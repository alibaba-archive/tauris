package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TLogger;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.bitwalker.useragentutils.UserAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 17/1/8.
 */
@Name("ua")
public class UserAgentFilter extends BaseTFilter {

    private TLogger logger;

    @Required
    String source;

    String target = "ua";

    /**
     * 解析成功后，移除$source字段
     */
    boolean remove = false;

    int cacheSize = 0;

    int cacheExpiredSeconds = 0;

    private LoadingCache<String, Map<String, String>> _cache = null;

    public void init() {
        this.logger = TLogger.getLogger(this);
        if (cacheSize > 0) {
            CacheBuilder<Object, Object> b = CacheBuilder.newBuilder().maximumSize(cacheSize);
            if (cacheExpiredSeconds > 0) {
                b.expireAfterAccess(cacheExpiredSeconds, TimeUnit.SECONDS);
            }
            _cache = b.build(new CacheLoader<String, Map<String, String>>() {
                public Map<String, String> load(String uastr) {
                    return parse(uastr);
                }
            });
        }
    }

    @Override
    public boolean doFilter(TEvent event) {
        String ua = (String) event.get(source);
        if (ua == null) return true;

        if (_cache == null) {
            event.set(target, parse(ua));
        } else {
            try {
                Map<String, String> o  = _cache.get(ua);
                event.set(target, o);
            } catch (ExecutionException e) {
                logger.ERROR("load iploc from cache exception", e);
                return true;
            } catch (CacheLoader.InvalidCacheLoadException e) {
                return true;
            }
        }
        if (remove) {
            event.remove(source);
        }
        return true;
    }

    private static Map<String, String> parse(String uastr) {
        UserAgent ua = new UserAgent(uastr);
        Map<String, String> o = new HashMap<>();
        o.put("os", ua.getOperatingSystem().name().toLowerCase());
        o.put("os_family", ua.getOperatingSystem().getGroup().getName().toLowerCase());
        o.put("device_type", ua.getOperatingSystem().getDeviceType().name().toLowerCase());
        o.put("browser", ua.getBrowser().name().toLowerCase());
        o.put("browser_family", ua.getBrowser().getGroup().getName().toLowerCase());
        o.put("browser_type", ua.getBrowser().getBrowserType().name().toLowerCase());
        if (ua.getBrowserVersion() != null) {
            o.put("browser_version", ua.getBrowserVersion().toString());
        }
        return o;
    }
}
