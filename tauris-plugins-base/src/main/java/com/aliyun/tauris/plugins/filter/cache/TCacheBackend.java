package com.aliyun.tauris.plugins.filter.cache;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TPluginInitException;

/**
 * Created by ZhangLei on 2018/5/21.
 */
public interface TCacheBackend extends TPlugin {

    void init() throws TPluginInitException ;
    void set(String key, Object value);

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在。
     * @param key cache key
     * @param value cache value
     * @return return true if key not exists
     */
    boolean setnx(String key, Object value);


    Object get(String key);
    void delete(String key);

    void destroy();
}
