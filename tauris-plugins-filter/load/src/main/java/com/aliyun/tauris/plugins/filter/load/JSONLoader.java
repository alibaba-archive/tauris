package com.aliyun.tauris.plugins.filter.load;

import com.alibaba.fastjson.JSON;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ZhangLei on 17/7/27.
 */
@Name("json")
public class JSONLoader implements TLoader {

    private final ReentrantLock lock = new ReentrantLock();

    private Map<String, Object> data = new HashMap<>();

    @Override
    public void unmarshal(String text) throws TPluginInitException {
        Map<String, Object> data;
        try {
            data = JSON.parseObject(text);
        } catch (Exception e) {
            throw new TPluginInitException("invalid text", e);
        }
        lock.lock();
        this.data.clear();
        this.data = Collections.unmodifiableMap(data);
        lock.unlock();
    }

    @Override
    public Map<String, Object> get() {
        lock.lock();
        try {
            return data == null ? Collections.emptyMap() : data;
        } finally {
            lock.unlock();
        }
    }
}
