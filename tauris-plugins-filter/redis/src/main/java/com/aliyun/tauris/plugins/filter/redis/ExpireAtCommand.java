package com.aliyun.tauris.plugins.filter.redis;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.formatter.SimpleFormatter;
import com.aliyun.tauris.expression.TExpression;
import redis.clients.jedis.Jedis;

/**
 * Created by ZhangLei on 2018/5/18.
 */
@Name("EXPIREAT")
public class ExpireAtCommand implements TRedisCommand {

    @Required
    SimpleFormatter key;

    @Required
    TExpression timestamp;

    @Override
    public Object execute(Jedis client, TEvent event) {
        String k = key.format(event);
        Long   t = timestamp.calc(event).longValue();
        return client.expireAt(k, t);
    }
}
