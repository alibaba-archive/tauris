package com.aliyun.tauris;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class TDictionary {

    public static final TDictionary global = new TDictionary();

    private Lock lock = new ReentrantLock();

    private ConcurrentHashMap<String, Map<String, String>> dicts = new ConcurrentHashMap<>();

    public TDictionary() {
    }

    public String get(String dictName, String key) {
        if (dictName == null) throw new IllegalArgumentException("dictName is null");
        if (key == null) throw new IllegalArgumentException("key is null");
        Map<String, String> dict = dicts.get(dictName);
        if (dict == null) {
            return null;
        }
        lock.lock();
        String value = dict.get(key);
        lock.unlock();
        return value;
    }

    public void put(String distName, String key, String value) {
        Map<String, String> d = dicts.get(distName);
        if (d == null) {
            d = new ConcurrentHashMap<>();
            Map<String, String> d1 = dicts.putIfAbsent(distName, d);
            if (d1 != null) {
                d = d1;
            }
        }
        d.put(key, value);
    }

    public void update(String dictName, Map<String, String> dict) {
        ConcurrentHashMap<String, String> d = new ConcurrentHashMap<>(dict);
        lock.lock();
        dicts.put(dictName, d);
        lock.unlock();
    }
}
