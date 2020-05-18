package com.aliyun.tauris.plugins.filter.redis;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.formatter.SimpleFormatter;
import redis.clients.jedis.Jedis;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("GET")
public class GetCommand implements TRedisCommand {

    @Required
    SimpleFormatter key;

    @Override
    public Object execute(Jedis client, TEvent event) {
        String k = key.format(event);
        return client.get(k);
    }
}
