package com.aliyun.tauris.plugins.filter.redis;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.formatter.SimpleFormatter;
import com.aliyun.tauris.expression.TExpression;
import redis.clients.jedis.Jedis;

/**
 * Created by ZhangLei on 2018/5/18.
 */
@Name("EXPIRE")
public class ExpireCommand implements TRedisCommand {

    @Required
    SimpleFormatter key;

    @Required
    TExpression ttl;

    @Override
    public Object execute(Jedis client, TEvent event) {
        String  k = key.format(event);
        Integer t = ttl.calc(event).intValue();
        return client.expire(k, t);
    }
}
