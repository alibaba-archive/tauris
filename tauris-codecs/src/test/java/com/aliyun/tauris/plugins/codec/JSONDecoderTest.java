package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ZhangLei on 2017/11/24.
 */
public class JSONDecoderTest {

    String simpleSource1;
    String simpleSource2;

    String complexSource;

    @Before
    public void init() {
        JSONObject jo1 = new JSONObject(){
            {
                put("name", "name1");
                put("type", "type1");
            }
        };
        JSONObject jo2 = new JSONObject(){
            {
                put("name", "name2");
                put("type", "type2");
            }
        };
        simpleSource1 = JSON.toJSONString(jo1);
        simpleSource2 = JSON.toJSONString(jo2);
        JSONObject os = new JSONObject();
        os.put("meta1", "metaval1");
        os.put("meta2", "metaval2");
        os.put("array", new JSONObject[]{jo1, jo2});

        complexSource = JSON.toJSONString(os);
    }

    @Test
    public void testSimpleToTarget() throws DecodeException {
        TEvent event = new DefaultEvent();
        JSONDecoder decoder = new JSONDecoder();
        decoder.decode(simpleSource1, event, "tgt");

        JSONObject o = (JSONObject)event.get("tgt");
        Assert.assertEquals("name1", o.getString("name"));
        Assert.assertEquals("type1", o.getString("type"));
    }
}
