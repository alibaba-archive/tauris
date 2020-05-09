package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ZhangLei on 2017/11/24.
 */
public class QueryStringDecoderTest {

    String source;

    @Test
    public void testQs() throws Exception {

        QueryStringDecoder decoder = new QueryStringDecoder();
        source = "a=1&b=2&c=3";

        TEvent event = new DefaultEvent();

        decoder.decode(source, event);
        Assert.assertEquals("1", event.get("a"));
        Assert.assertEquals("2", event.get("b"));
        Assert.assertEquals("3", event.get("c"));
    }

    @Test
    public void testWithArray() throws Exception{
        QueryStringDecoder decoder = new QueryStringDecoder();
        decoder.arrayValue = true;
        decoder.excludes = Sets.newHashSet("b");

        source = "a=1&b=2&c=3&c=4";

        TEvent event = new DefaultEvent();
        decoder.decode(source, event);

        Assert.assertEquals("1", event.get("a"));
        Assert.assertNull(event.get("b"));
        Object c = event.get("c");
        Assert.assertTrue(c instanceof List);
        List lst = (List)c;

        Assert.assertEquals("3", lst.get(0));
        Assert.assertEquals("4", lst.get(1));
    }

    @Test
    public void testWithTarget() throws Exception{
        QueryStringDecoder decoder = new QueryStringDecoder();

        decoder.includes = Sets.newHashSet("a", "c");
        decoder.arrayValue = true;
        decoder.overwrite = true;
        TEvent event = new DefaultEvent();
        event.set("c", "5");

        decoder.decode("a=1&b=2&c=3&c=4", event, "tgt");

        Assert.assertEquals("1", event.get("tgt.a"));
        Assert.assertNull(event.get("tgt.b"));
        Object c = event.get("tgt.c");
        Assert.assertTrue(c instanceof List);
        List lst = (List)c;

        Assert.assertTrue(lst.contains("3"));
        Assert.assertTrue(lst.contains("4"));

    }

    @Test
    public void testNoOverwrite() throws Exception{
        QueryStringDecoder decoder = new QueryStringDecoder();

        decoder.overwrite = false;
        decoder.arrayValue = false;
        TEvent event = new DefaultEvent();
        event.set("a", 10);

        decoder.decode("a=1&b=2&c=3&c=4", event);

        Assert.assertEquals(10, event.get("a"));
        Assert.assertEquals("2", event.get("b"));
    }
}
