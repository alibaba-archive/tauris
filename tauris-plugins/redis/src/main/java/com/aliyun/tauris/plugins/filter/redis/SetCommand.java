package com.aliyun.tauris.plugins.filter.redis;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.formatter.SimpleFormatter;
import redis.clients.jedis.Jedis;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("SET")
public class SetCommand implements TRedisCommand {

    @Required
    SimpleFormatter key;

    @Required
    SimpleFormatter value;

    @Override
    public Object execute(Jedis client, TEvent event) {
        String k = key.format(event);
        String v = value.format(event);
        return client.set(k, v);
    }
}
