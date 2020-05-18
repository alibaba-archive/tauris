package com.aliyun.tauris;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;


/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class DefaultEventTest {

    @Test
    public void test() throws Exception {

        TEvent e = new DefaultEvent("testcase");
        String json = String.join(",", IOUtils.readLines(getClass().getClassLoader().getResourceAsStream("jsondata.json")));
        JSONObject data = JSON.parseObject(json);
        data.forEach(e::set);

        Assert.assertEquals("flat", e.get("static"));
        Assert.assertNull(e.get("request.hello.test.4.5"));

        Assert.assertEquals("GET", e.get("request.method"));
        Assert.assertEquals("world", e.get("request.headers.test.hello"));

        e.set("request.headers.test.hello", "testcase");
        try {
            e.set("request.headers.test.hello.gg.ee", "testcase");
            Assert.assertFalse(true);
        } catch (IllegalArgumentException ex) {}

        Assert.assertEquals("testcase", e.get("request.headers.test.hello"));

        Map m = (Map)e.remove("request.headers.test");
        Assert.assertEquals("testcase", m.get("hello"));

        Assert.assertNull(e.get("request.hello.test"));
        Assert.assertNull(e.get("request.hello.test.2"));
        Assert.assertNull(e.get("request.hello.test.3.4"));

        Assert.assertEquals(2, e.get("request.intarray[1]"));
        Assert.assertEquals("ddd", e.get("request.strarray[3]"));
        Assert.assertEquals("eee", e.get("request.strarray[-1]"));
        Assert.assertEquals("ccc", e.get("request.strarray[-3]"));
        Object mi = e.get("@timestamp.millis");
//        System.out.println(mi);
//        System.out.println(mi.getClass());

        Assert.assertNull(e.get("notexists"));

    }

}
