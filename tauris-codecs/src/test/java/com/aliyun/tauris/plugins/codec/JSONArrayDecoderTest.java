package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ZhangLei on 2017/11/24.
 */
public class JSONArrayDecoderTest {

    String stringSource;

    String objectSource;

    @Before
    public void init() {
        stringSource = JSON.toJSONString(new String[]{"value1", "value2"});//"[\"value1\", \"value2\"]";

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
        JSONObject[] os = new JSONObject[]{jo1, jo2};
        objectSource = JSON.toJSONString(os);
    }

    @Test
    public void test() throws DecodeException {
        JSONArrayDecoder decoder = new JSONArrayDecoder();

        TEvent event = new DefaultEvent();
        decoder.decode(stringSource, event, "array");

        JSONArray ary = (JSONArray)event.get("array");
        Assert.assertEquals(2, ary.size());
    }

    @Test
    public void test2() throws DecodeException {
        JSONArrayDecoder decoder = new JSONArrayDecoder();

        TEvent event = new DefaultEvent();
        decoder.decode(objectSource, event, "array");
        JSONArray ary = (JSONArray)event.get("array");
        Assert.assertEquals(2, ary.size());

        Assert.assertEquals("name1", ary.getJSONObject(0).getString("name"));
        Assert.assertEquals("type1", ary.getJSONObject(0).getString("type"));
        Assert.assertEquals("name2", ary.getJSONObject(1).getString("name"));
        Assert.assertEquals("type2", ary.getJSONObject(1).getString("type"));
    }

    @Test
    public void testDecodeText() throws Exception {
//        JSONArrayDecoder decoder = new JSONArrayDecoder();
//        DecodeReader reader = decoder.wrap(new StringReader(stringSource));
//        int i = 1;
//        while (true) {
//            try {
//                TEvent event = reader.read();
//                Assert.assertEquals("value" + i, event.getSource());
//                i++;
//            } catch (EOFException e) {
//                reader.close();
//                break;
//            }
//        }
    }

    public void testDecodeObject() throws Exception {
//        JSONArrayDecoder decoder = new JSONArrayDecoder();
//        DecodeReader reader = decoder.wrap(new StringReader(objectSource));
//        int i = 1;
//        while (true) {
//            try {
//                TEvent event = reader.read();
//                Assert.assertEquals("name" + i, event.get("name"));
//                Assert.assertEquals("type" + i, event.get("type"));
//                i++;
//            } catch (EOFException e) {
//                reader.close();
//                break;
//            }
//        }
    }
}
