package com.aliyun.tauris.plugins.filter.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ZhangLei on 2018/5/18.
 */
public class BackendPools {

    private static Map<String, TCacheBackend> backends = new ConcurrentHashMap<>();

    public static void setBackend(String name, TCacheBackend backend) {
        backends.put(name, backend);
    }

    public static TCacheBackend getBackend(String name) {
        return backends.get(name);
    }
}
