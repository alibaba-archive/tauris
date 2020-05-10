package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.DefaultEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ZhangLei on 2017/11/24.
 */
public class JSONEncoderTest {

    @Test
    public void test() throws EncodeException, IOException {
        DefaultEvent event = new DefaultEvent();
        event.setField("f1", "v1");
        event.setField("f2", "v2");
        event.setField("f3", "v3");
        event.setField("ig1", "g1");

        JsoniterEncoder encoder = new JsoniterEncoder();
        encoder.init();
        ByteArrayOutputStream sw = new ByteArrayOutputStream();
        encoder.encode(event, sw);
        Assert.assertFalse(sw.toString().contains("ig1"));
        Assert.assertFalse(sw.toString().contains("\n"));

        encoder.init();
        sw = new ByteArrayOutputStream();
        encoder.encode(event, sw);
        sw.flush();
        sw.close();
        Assert.assertTrue(sw.toString().contains("\n"));

        sw = new ByteArrayOutputStream();
        encoder.init();
        encoder.encode(event, sw);
        Assert.assertFalse(sw.toString().contains("f3"));
        Assert.assertFalse(sw.toString().contains("ig1"));
    }
}
