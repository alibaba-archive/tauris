package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by ZhangLei on 2017/11/24.
 */
public class JSONEncoderTest {

    @Test
    public void test() throws EncodeException, IOException {
        TEvent event = new TEvent();
        event.setField("f1", "v1");
        event.setField("f2", "v2");
        event.setField("f3", "v3");
        event.setField("ig1", "g1");

        JSONEncoder encoder = new JSONEncoder();
        encoder.excludes = new String[]{"ig1"};
        encoder.init();
        ByteArrayOutputStream sw = new ByteArrayOutputStream();
        encoder.encode(event, sw);
        Assert.assertFalse(sw.toString().contains("ig1"));
        Assert.assertFalse(sw.toString().contains("\n"));


        encoder.pretty = true;
        encoder.init();
        sw = new ByteArrayOutputStream();
        encoder.encode(event, sw);
        sw.flush();
        sw.close();
        Assert.assertTrue(sw.toString().contains("\n"));

        sw = new ByteArrayOutputStream();
        encoder.excludes = null;
        encoder.includes = new String[] {"f1", "f2"};
        encoder.init();
        encoder.encode(event, sw);
        Assert.assertFalse(sw.toString().contains("f3"));
        Assert.assertFalse(sw.toString().contains("ig1"));
    }
}
