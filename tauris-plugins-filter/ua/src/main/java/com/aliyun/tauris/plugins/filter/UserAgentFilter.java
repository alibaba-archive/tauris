package com.aliyun.tauris.plugins.filter;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Created by ZhangLei on 17/1/8.
 */
@Name("ua")
public class UserAgentFilter extends BaseTFilter {

    private static Logger logger = LoggerFactory.getLogger(UserAgentFilter.class);

    @Required
    String source;

    String target = "ua";

    int cacheSize = 0;

    /**
     * 解析成功后，移除$source字段
     */
    boolean remove = false;

    private Map<String, JSONObject> cache = null;

    public void init() {
        if (cacheSize > 0) {
            cache = Collections.synchronizedMap(new LRUMap<>(cacheSize));
        }
    }

    @Override
    public boolean doFilter(TEvent event) {
        String ua = (String) event.get(source);
        if (ua == null) return true;

        JSONObject u = parse(ua);
        if (u != null) {
            event.set(target, u);
        }
        if (remove) {
            event.remove(source);
        }
        return true;
    }

    private JSONObject parse(String uastr) {
        if (cache != null) {
            JSONObject ox = cache.get(uastr);
            if (ox != null) {
                return ox;
            }
        }
        JSONObject o = new JSONObject();
        UserAgent ua = new UserAgent(uastr);
        o.put("os", ua.getOperatingSystem().name().toLowerCase());
        o.put("os_family", ua.getOperatingSystem().getGroup().getName().toLowerCase());
        o.put("device_type", ua.getOperatingSystem().getDeviceType().name().toLowerCase());
        o.put("browser", ua.getBrowser().name().toLowerCase());
        o.put("browser_family", ua.getBrowser().getGroup().getName().toLowerCase());
        o.put("browser_type", ua.getBrowser().getBrowserType().name().toLowerCase());
        if (ua.getBrowserVersion() != null) {
            o.put("browser_version", ua.getBrowserVersion().toString());
        }
        if (cache != null) {
            cache.put(uastr, o);
        }
        return o;
    }
}
