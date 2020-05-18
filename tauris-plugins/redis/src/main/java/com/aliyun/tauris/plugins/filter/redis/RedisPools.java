package com.aliyun.tauris.plugins.filter.redis;

import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class RedisPools {

    private static Map<String, JedisPool> pools = new ConcurrentHashMap<>();

    public static void setPool(String name, JedisPool pool) {
        pools.put(name, pool);
    }

    public static JedisPool getPool(String name) {
        return pools.get(name);
    }
}
