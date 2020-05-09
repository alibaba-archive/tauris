package com.aliyun.tauris.plugins.codec;

/**
 * Created by ZhangLei on 2018/6/5.
 */

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.Base64;

/**
 * @author yundun-waf-dev
 * @date 2018-06-05
 */
public class B64DecodeTest {

    @Test
    public void test() throws Exception {
        Base64Decoder decoder = new Base64Decoder();

        String text = "a text";
        TEvent event = new DefaultEvent();

        decoder.decode(Base64.getEncoder().encodeToString(text.getBytes()), event, "tgt");

        Assert.assertEquals(text, event.get("tgt"));
    }

}
