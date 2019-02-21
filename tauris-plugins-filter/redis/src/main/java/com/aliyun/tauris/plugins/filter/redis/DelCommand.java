package com.aliyun.tauris.plugins.filter.redis;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.formatter.SimpleFormatter;
import redis.clients.jedis.Jedis;

/**
 * Created by ZhangLei on 2018/5/18.
 */
@Name("DEL")
public class DelCommand implements TRedisCommand {

    @Required
    SimpleFormatter key;

    @Override
    public Object execute(Jedis client, TEvent event) {
        String k = key.format(event);
        return client.del(k);
    }
}
