package com.aliyun.tauris.plugins.filter.redis;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;
import redis.clients.jedis.Jedis;

/**
 * Created by ZhangLei on 2018/5/18.
 */
@Type
public interface TRedisCommand extends TPlugin {

    Object execute(Jedis client, TEvent event);

}
