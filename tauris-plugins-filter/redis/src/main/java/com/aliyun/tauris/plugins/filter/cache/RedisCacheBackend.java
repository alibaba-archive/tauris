package com.aliyun.tauris.plugins.filter.cache;

import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 2018/5/21.
 */
@Name("redis")
public class RedisCacheBackend implements TCacheBackend {

    @Required
    String host;

    Integer port = 6379;

    Integer timeout = 15000;

    String password;

    Integer database = 0;

    Integer expired;

    GenericObjectPoolConfig config = new GenericObjectPoolConfig();

    private JedisPool jedisPool;

    @Override
    public void init() throws TPluginInitException {
        jedisPool = new JedisPool(config, host, port, timeout, password, database);
    }

    @Override
    public void set(String key, Object value) {
        if (value == null) {
            return;
        }
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value.toString());
            if (expired != null) {
                jedis.expire(key, expired);
            }
        }
    }

    @Override
    public boolean setnx(String key, Object value) {
        if (value == null) {
            return false;
        }
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.setnx(key, value.toString());
            if (expired != null) {
                jedis.expire(key, expired);
            }
            return true;
        }
    }

    @Override
    public Object get(String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public void delete(String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    @Override
    public void destroy() {
        jedisPool.close();
    }
}
