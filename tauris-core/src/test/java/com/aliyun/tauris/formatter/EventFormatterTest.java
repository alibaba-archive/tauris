package com.aliyun.tauris.formatter;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;


/**
 * Created by ZhangLei on 16/12/21.
 */
public class EventFormatterTest {

    @Test
    public void test1() {
        String expr = "hello %{name}";

        TEvent e = new DefaultEvent("");
        e.set("name", "world");
        EventFormatter o = EventFormatter.build(expr);
        String result = o.format(e);
        Assert.assertEquals("hello world", result);
    }

    @Test
    public void test11() {
        String expr = "hello %{name} %{noname?-}";

        TEvent e = new DefaultEvent("");
        e.set("name", "world");
        EventFormatter o = EventFormatter.build(expr);
        String result = o.format(e);
        Assert.assertEquals("hello world -", result);
    }

    @Test
    public void test2() {
        DateTimeFormatter sdf = DateTimeFormat.forPattern("yyyy-MM-dd");
        String expr = "today is %{+yyyy-MM-dd}!";

        TEvent e = new DefaultEvent("");
        EventFormatter o = EventFormatter.build(expr);
        String result = o.format(e);
        Assert.assertEquals("today is " + new DateTime().toString(sdf) + "!", result);
    }

    @Test
    public void test3() {
        DateTimeFormatter sdf = DateTimeFormat.forPattern("yyyy.MM.dd");
        String expr = "data/es/%{+yyyy.MM.dd}.dat";

        TEvent e = new DefaultEvent("");
        EventFormatter o = EventFormatter.build(expr);
        String result = o.format(e);
        Assert.assertEquals("data/es/" + new DateTime().toString(sdf) + ".dat", result);
    }

    @Test
    public void test31() {
        DateTimeFormatter sdf = DateTimeFormat.forPattern("yyyy.MM.dd");
        String expr = "data/es/%{+yyyy.MM.dd}.dat";

        EventFormatter o = EventFormatter.build(expr);
        String result = o.format();
        Assert.assertEquals("data/es/" + new DateTime().toString(sdf) + ".dat", result);
    }


    @Test
    public void testMeta() {
        TEvent e = new DefaultEvent("");
        e.addMeta("m", "v");
        EventFormatter o = EventFormatter.build("%{@m}");
        String result = o.format(e);
        Assert.assertEquals("v", result);
    }


    @Test
    public void testMeta1() {
        TEvent e = new DefaultEvent("");
        e.addMeta("m", "v");
        EventFormatter o = EventFormatter.build("%{@m}, noname %{@x?empty}");
        String result = o.format(e);
        Assert.assertEquals("v, noname empty", result);
    }

    @Test
    public void testEnv() {
        Map<String, String> env = System.getenv();
        String expr = "data%{!HOME}";
        Assert.assertEquals("data" + env.get("HOME"), EventFormatter.build(expr).format(null));
    }

    @Test
    public void testEnv1() {
        Map<String, String> env = System.getenv();
        String expr = "data%{!HOME} %{!NONAME?notexists}";
        Assert.assertEquals("data" + env.get("HOME") + " notexists", EventFormatter.build(expr).format(null));
    }

}
