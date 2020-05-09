package com.aliyun.tauris.plugins.codec;

/**
 * Created by ZhangLei on 2018/6/5.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.DefaultEventFactory;
import com.aliyun.tauris.TScanner;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @author yundun-waf-dev
 * @date 2018-06-05
 */
public class MultilineDecodeTest {

    @Test
    public void test() throws Exception {
        JSONObject o1 = new JSONObject();
        o1.put("name", "name1");
        o1.put("value", "value1");

        JSONObject o2 = new JSONObject();
        o2.put("name", "name2");
        o2.put("value", "value2");

        String source = JSON.toJSONString(o1, true) + "\n"+ JSON.toJSONString(o2, true);

        JSONDecoder             decoder = new JSONDecoder();
        MatchlineScannerBuilder scanner = new MatchlineScannerBuilder();
        scanner.head = null; //Pattern.compile("");
        scanner.tail = Pattern.compile("^\\}$");
        scanner.codec = decoder;

        TScanner s = scanner.create(new ByteArrayInputStream(source.getBytes()), new DefaultEventFactory());
        AtomicInteger index = new AtomicInteger(1);
        s.scan((e) -> {
            int i = index.getAndIncrement();
            Assert.assertEquals("name" + i, e.get("name"));
            Assert.assertEquals("value" + i, e.get("value"));
            return true;
        });
    }

}
