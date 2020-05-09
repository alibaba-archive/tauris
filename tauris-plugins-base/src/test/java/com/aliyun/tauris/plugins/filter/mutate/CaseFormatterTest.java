package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import com.google.common.base.CaseFormat;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ZhangLei on 17/7/27.
 */
public class CaseFormatterTest {


    @Test
    public void testKeyModeAll() {
        //所有field的大写变小写

        CaseFormatter rf = new CaseFormatter();
        rf.mode = CaseFormatter.Mode.key;
        rf.from = CaseFormat.UPPER_CAMEL;
        rf.to = CaseFormat.LOWER_CAMEL;
        rf.init();

        TEvent e = new DefaultEvent("");
        e.set("f1", "v1");
        e.set("F2", "v2");
        e.set("f3", "V3");

        rf.mutate(e);

        Assert.assertEquals("v1", e.get("f1"));
        Assert.assertEquals("v2", e.get("f2"));
        Assert.assertEquals("V3", e.get("f3"));

        Assert.assertNotNull(e.get("f1"));
        Assert.assertNull(e.get("F2"));
        Assert.assertNotNull(e.get("f3"));
    }

    @Test
    public void testKeyModeSome() {
        //部分指定field的小写变大写

        CaseFormatter rf = new CaseFormatter();
        rf.mode = CaseFormatter.Mode.key;
        rf.from = CaseFormat.LOWER_CAMEL;
        rf.to = CaseFormat.UPPER_CAMEL;
        rf.fields = new String[]{"f2", "f3"};
        rf.init();

        TEvent e = new DefaultEvent("");
        e.set("f1", "v1");
        e.set("f2", "v2");
        e.set("f3", "V3");

        rf.mutate(e);

        Assert.assertEquals("v1", e.get("f1"));
        Assert.assertEquals("v2", e.get("F2"));
        Assert.assertEquals("V3", e.get("F3"));

        Assert.assertNotNull(e.get("f1"));
        Assert.assertNull(e.get("f2"));
        Assert.assertNull(e.get("f3"));
    }

    @Test
    public void testValueModelAll() {
        //所有value的大写变小写

        CaseFormatter rf = new CaseFormatter();
        rf.mode = CaseFormatter.Mode.value;
        rf.from = CaseFormat.UPPER_CAMEL;
        rf.to = CaseFormat.LOWER_CAMEL;
        rf.init();

        TEvent e = new DefaultEvent("");
        e.set("f1", "v1");
        e.set("F2", "v2");
        e.set("f3", "V3");

        rf.mutate(e);

        Assert.assertEquals("v1", e.get("f1"));
        Assert.assertEquals("v2", e.get("F2"));
        Assert.assertEquals("v3", e.get("f3"));

        Assert.assertNotNull(e.get("f1"));
        Assert.assertNotNull(e.get("F2"));
        Assert.assertNotNull(e.get("f3"));
    }

    @Test
    public void testValueModeSome() {
        //部分指定field的小写变大写

        CaseFormatter rf = new CaseFormatter();
        rf.mode = CaseFormatter.Mode.value;
        rf.from = CaseFormat.LOWER_CAMEL;
        rf.to = CaseFormat.UPPER_CAMEL;
        rf.fields = new String[]{"F2", "f3"};
        rf.init();

        TEvent e = new DefaultEvent("");
        e.set("f1", "v1");
        e.set("F2", "v2");
        e.set("f3", "V3");

        rf.mutate(e);

        Assert.assertEquals("v1", e.get("f1"));
        Assert.assertEquals("V2", e.get("F2"));
        Assert.assertEquals("V3", e.get("f3"));

        Assert.assertNotNull(e.get("f1"));
        Assert.assertNotNull(e.get("F2"));
        Assert.assertNotNull(e.get("f3"));
    }

}
