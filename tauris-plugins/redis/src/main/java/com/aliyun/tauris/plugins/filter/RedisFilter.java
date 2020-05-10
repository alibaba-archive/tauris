package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.plugins.filter.redis.RedisPools;
import com.aliyun.tauris.plugins.filter.redis.TRedisCommand;
import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by ZhangLei on 17/1/8.
 */
@Name("redis")
public class RedisFilter extends BaseTFilter {

    private static Logger logger = LoggerFactory.getLogger(RedisFilter.class);

    String pool = "default";

    String host;

    Integer port = 6379;

    Integer timeout = 15000;

    String password;

    String target;

    Integer database = 0;

    @Required
    TRedisCommand command;

    GenericObjectPoolConfig config = new GenericObjectPoolConfig();

    private JedisPool jedisPool;

    public void init() throws TPluginInitException {
        if (pool == null && host == null) {
            throw new TPluginInitException("pool_name or host must be set");
        }
    }

    @Override
    public void prepare() throws TPluginInitException {
        jedisPool = RedisPools.getPool(pool);
        if (jedisPool != null) {
            return;
        }
        jedisPool = new JedisPool(config, host, port, timeout, password, database);
        RedisPools.setPool(pool, jedisPool);
    }

    @Override
    public boolean doFilter(TEvent event) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = command.execute(jedis, event);
            if (target != null) {
                event.set(target, result);
            }
        }
        return true;
    }

    @Override
    public void release() {
        super.release();
        this.jedisPool.destroy();
    }
}
