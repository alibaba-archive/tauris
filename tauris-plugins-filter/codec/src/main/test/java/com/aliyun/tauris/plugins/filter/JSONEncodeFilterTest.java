package com.aliyun.tauris.plugins.filter;

/**
 * Created by ZhangLei on 2018/6/5.
 */

import com.alibaba.fastjson.JSON;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TFilter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Base64;

/**
 * @author yundun-waf-dev
 * @date 2018-06-05
 */
public class JSONEncodeFilterTest {

    @Test
    public void test() throws Exception {
        String cfg = "encode { encoder => json { target => 'tgt'; } }";
        TFilter filter = ConfigTestBuilder.buildFilters(cfg).get(0);
        filter.prepare();
        TEvent event = new TEvent("");
        event.set("a", "1");
        event.set("b", "2");
        String jstxt = JSON.toJSONString(event.getFields());
        filter.filter(event);
        Assert.assertEquals(jstxt, event.get("tgt"));
    }

}
