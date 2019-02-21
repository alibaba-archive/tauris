package com.aliyun.tauris.plugins.filter;

import redis.clients.jedis.Jedis;

/**
 * Created by ZhangLei on 2018/5/18.
 */
public class RedisTest {

    public static void main(String[] argv) {
        String host = "10.218.141.225";
        Jedis jedis = new Jedis(host, 6379, 5000);
        jedis.auth("gaA8NcFoimm2Qkiv");
        String r = jedis.set("hello", "world");
        System.out.println(r);
    }
}
