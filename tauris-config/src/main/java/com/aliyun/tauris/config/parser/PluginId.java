package com.aliyun.tauris.config.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class PluginId
 *
 * @author yundun-waf-dev
 * @date 2019-04-17
 */
public class PluginId {

    private static final Map<String, AtomicInteger> counter = new HashMap<>();

    public static String generateId(String typeName) {
        synchronized (counter) {
            AtomicInteger c = counter.get(typeName);
            if (c == null) {
                c = new AtomicInteger(0);
                counter.put(typeName, c);
            }
            int n = c.incrementAndGet();
            return String.format("%s_%d", typeName, n);
        }
    }
}
