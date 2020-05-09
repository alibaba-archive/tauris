package com.aliyun.tauris.plugins.filter;

/**
 * Created by ZhangLei on 2018/6/5.
 */

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TFilter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Base64;

/**
 * @author yundun-waf-dev
 * @date 2018-06-05
 */
public class B64EncodeFilterTest {

    @Test
    public void test() throws Exception {
        String cfg = "encode { encoder => base64 { source => '@source'; target => 'tgt'; } }";
        String text = "川石";
        TFilter filter = ConfigTestBuilder.buildFilters(cfg).get(0);
        filter.prepare();
        TEvent event = new DefaultEvent(text);
        filter.filter(event);
        Assert.assertEquals(Base64.getEncoder().encodeToString(text.getBytes()), event.get("tgt"));
    }

}
