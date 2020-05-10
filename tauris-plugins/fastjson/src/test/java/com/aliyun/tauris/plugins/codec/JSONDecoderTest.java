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

    @Test
    public void testSimpleReader() throws IOException, DecodeException {
//        JSONDecoder decoder = new JSONDecoder();
//        DecodeReader reader = decoder.wrap(new StringReader(simpleSource1 + "\n" + simpleSource2));
//
//        int i = 1;
//        while(true) {
//            try {
//                TEvent event = reader.read();
//                Assert.assertEquals("type" + i, event.get("type"));
//                i++;
//            } catch (EOFException e) {
//                reader.close();
//                break;
//            } catch (Exception e) {
//                Assert.assertFalse(true);
//            }
//        }
    }

    @Test
    public void testComplexReader() throws IOException, DecodeException {
//        JSONDecoder decoder = new JSONDecoder();
//        DecodeReader reader = decoder.wrap(new StringReader(complexSource));
//
//        decoder.subKey = "array";
//        decoder.metaKeys = new String[] { "meta1", "meta2"};
//
//        decoder.init();
//
//        int i = 1;
//        while(true) {
//            try {
//                TEvent event = reader.read();
//                Assert.assertEquals("name" + i, event.get("name"));
//                Assert.assertEquals("type" + i, event.get("type"));
//                Assert.assertEquals("metaval1", event.get("@meta1"));
//                Assert.assertEquals("metaval2" , event.get("@meta2"));
//                i++;
//            } catch (EOFException e) {
//                reader.close();
//                break;
//            } catch (Exception e) {
//                Assert.assertFalse(true);
//            }
//        }
    }

    @Test
    public void testSimpleToTargetWithNoise() throws DecodeException {
        TEvent event = new DefaultEvent();
        JSONDecoder decoder = new JSONDecoder();
        String source = "20181125 - " + simpleSource1;
        decoder.decode(source, event, "tgt");

        JSONObject o = (JSONObject)event.get("tgt");
        Assert.assertEquals("name1", o.getString("name"));
        Assert.assertEquals("type1", o.getString("type"));


        source = source + " suffix";
        decoder.decode(source, event, "tgt");

        o = (JSONObject)event.get("tgt");
        Assert.assertEquals("name1", o.getString("name"));
        Assert.assertEquals("type1", o.getString("type"));

        source = simpleSource1 + " suffix";
        decoder.decode(source, event, "tgt");

        o = (JSONObject)event.get("tgt");
        Assert.assertEquals("name1", o.getString("name"));
        Assert.assertEquals("type1", o.getString("type"));
    }

}
