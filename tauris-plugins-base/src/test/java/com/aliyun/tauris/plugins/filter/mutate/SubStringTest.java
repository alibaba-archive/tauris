package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ZhangLei on 17/7/27.
 */
public class SubStringTest {

    @Test
    public void testIndex1() throws Exception {
        String str = RandomStringUtils.random(100);
        SubString ss = new SubString();
        ss.beginIndex = 10;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals(str.substring(10), event.getSource());
    }

    @Test
    public void testIndex2() throws Exception  {
        String str = RandomStringUtils.random(100);
        SubString ss = new SubString();
        ss.beginIndex = 10;
        ss.endIndex = 20;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals(str.substring(10, 20), event.getSource());
    }

    @Test
    public void testIndex3() throws Exception  {
        String str = RandomStringUtils.random(10);
        SubString ss = new SubString();
        ss.beginIndex = 10;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals(str, event.getSource());
    }

    @Test
    public void testIndex4() throws Exception  {
        String str = RandomStringUtils.random(100);
        SubString ss = new SubString();
        ss.beginIndex = 10;
        ss.endIndex = 500;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals(str.substring(10), event.getSource());
    }


    @Test
    public void testChars1() throws Exception  {
        String str = "502";
        SubString ss = new SubString();
        ss.maxStrlen = 5;
        ss.beginChars = " ,";
        ss.matchEnd = true;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals("502", event.getSource());
    }

    @Test
    public void testChars2() throws Exception  {
        String str = "-,503";
        SubString ss = new SubString();
        ss.maxStrlen = 5;
        ss.beginChars = " ,";
        ss.matchEnd = true;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals("503", event.getSource());
    }

    @Test
    public void testChars3() throws Exception  {
        String str = "501,502, 503, 504";
        SubString ss = new SubString();
        ss.maxStrlen = 5;
        ss.beginChars = " ,";
        ss.matchEnd = true;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals("504", event.getSource());
    }

    @Test
    public void testChars3_1() throws Exception  {
        String str = "501,502, 503, 504";
        SubString ss = new SubString();
        ss.maxStrlen = 5;
        ss.beginChars = " ,";
        ss.endChars = ":";
        ss.matchEnd = true;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals("504", event.getSource());
    }

    @Test
    public void testChars4() throws Exception  {
        String str = "10.10.10.0:80";
        SubString ss = new SubString();
        ss.maxStrlen = 50;
        ss.beginChars = " ,";
        ss.endChars = ":";
        ss.matchEnd = true;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals("10.10.10.0", event.getSource());
    }

    @Test
    public void testChars5() throws Exception  {
        String str = "10.10.10.0:80,10.10.10.1:443, 10.10.10.2:80";
        SubString ss = new SubString();
        ss.maxStrlen = 50;
        ss.beginChars = " ,";
        ss.endChars = ":";
        ss.matchEnd = true;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals("10.10.10.2", event.getSource());
    }

    @Test
    public void testChars6() throws Exception  {
        String str = "10.10.10.0:80,10.10.10.1:443, 10.10.10.2:80";
        SubString ss = new SubString();
        ss.maxStrlen = 50;
        ss.beginChars = " ,";
        ss.endChars = ":";
        ss.matchEnd = false;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals("10.10.10.1", event.getSource());
    }

    @Test
    public void testChars7() throws Exception  {
        String str = "10.10.10.0:80,10.10.10.1:443, 10.10.10.2:80";
        SubString ss = new SubString();
        ss.maxStrlen = 50;
        ss.beginChars = " ,";
        ss.endChars = " ";
        ss.matchEnd = false;
        ss.init();
        TEvent event = new TEvent(str);

        ss.mutate(event);
        Assert.assertEquals("10.10.10.1:443,", event.getSource());
    }
}
