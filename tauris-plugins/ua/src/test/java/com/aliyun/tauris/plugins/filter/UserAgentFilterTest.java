package com.aliyun.tauris.plugins.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class UserAgentFilterTest {


    @Test
    public void test() throws Exception {
        UserAgentFilter uf = new UserAgentFilter();
        uf.cacheSize = 10000;
        uf.source = "ua";
        uf.init();
//        BufferedReader fr = new BufferedReader(new FileReader("//Users/ZhangLei/Work/Projects/ware/tauris/ua.log"));
        BufferedReader fr = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ua.txt")));
        String line = fr.readLine();
        long mi = System.currentTimeMillis();

        BiConsumer<String, Map<String, AtomicInteger>> cx = (k, c) -> {
            AtomicInteger x = c.get(k);
            if (x == null) {
                x = new AtomicInteger();
                c.put(k, x);
            }
            x.incrementAndGet();
        };
        int c = 0;
        Map<String, AtomicInteger> dc = new HashMap<>();
        TEvent ex = new DefaultEvent("");
        while (line != null) {
            ex.setField("ua", line.trim());
            uf.filter(ex);
            JSONObject ua = (JSONObject)ex.get("ua");
            System.out.println(line.trim());
            System.out.println("\t\t" + JSON.toJSONString(ua, true));
            line = fr.readLine();
        }
        for (Map.Entry<String, AtomicInteger> e : dc.entrySet()) {
            System.out.println(String.format("%s=%s", e.getKey(), e.getValue()));
        }
        System.out.println(System.currentTimeMillis() - mi);
        System.out.println(c);

        System.out.println("============");
        String uastr = "iPhone; iOS 11.3.1; Scale/3.00";
        UserAgent ua = new UserAgent(uastr);
        System.out.println(JSON.toJSONString(ua , true));
    }

}
