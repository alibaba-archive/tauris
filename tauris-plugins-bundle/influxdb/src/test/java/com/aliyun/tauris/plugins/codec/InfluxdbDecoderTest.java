package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.TEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Class InfluxdbDecoderTest
 *
 * @author zhanglei
 * @date 2018-09-04
 */
public class InfluxdbDecoderTest {

    @Test
    public void test() throws Exception {
        InfluxdbDecoder decoder = new InfluxdbDecoder();
        String line = "source_region,agent=localhost.xz,product=waf,source_region=西藏,warehouse=nt99 error=0.0,request_bytes=2293.0,response_bytes=3705.0,s2xx=2.0,s3xx=0.0,s4xx=0.0,s5xx=0.0,total=2.0,us2xx=2.0,us3xx=0.0,us4xx=0.0,us5xx=0.0 1536071700000000000";
        TEvent event = decoder.decode(line);

        Assert.assertEquals("source_region", event.getMeta("measurement"));

        Map<String, String> tags = (Map<String, String>)event.getMeta("tags");
        Assert.assertEquals("waf", tags.get("product"));
        Assert.assertEquals("西藏", tags.get("source_region"));

        Assert.assertEquals(2293.0, event.get("request_bytes"));
        Assert.assertEquals(1536071700000000000l / 1000000, event.getTimestamp().getMillis());

        line = "source_region,agent=localhost.xz,product=waf,source_region=西藏,warehouse=nt99 error=0.0,request_bytes=2293.0,response_bytes=3705.0,s2xx=2.0,s3xx=0.0,s4xx=0.0,s5xx=0.0,total=2.0,us2xx=2.0,us3xx=0.0,us4xx=0.0,us5xx=0.0 1536071700000000";
        decoder.precision = InfluxdbDecoder.Precision.microsecond;
        event = decoder.decode(line);
        Assert.assertEquals(1536071700000000000l / 1000000, event.getTimestamp().getMillis());

        line = "source_region,agent=localhost.xz,product=waf,source_region=西藏,warehouse=nt99 error=0.0,request_bytes=2293.0,response_bytes=3705.0,s2xx=2.0,s3xx=0.0,s4xx=0.0,s5xx=0.0,total=2.0,us2xx=2.0,us3xx=0.0,us4xx=0.0,us5xx=0.0 1536071700000";
        decoder.precision = InfluxdbDecoder.Precision.millisecond;
        event = decoder.decode(line);
        Assert.assertEquals(1536071700000000000l / 1000000, event.getTimestamp().getMillis());


        line = "source_region,agent=localhost.xz,product=waf,source_region=西藏,warehouse=nt99 error=0.0,request_bytes=2293.0,response_bytes=3705.0,s2xx=2.0,s3xx=0.0,s4xx=0.0,s5xx=0.0,total=2.0,us2xx=2.0,us3xx=0.0,us4xx=0.0,us5xx=0.0 2018-09-04T10:32:00Z";
        decoder.precision = InfluxdbDecoder.Precision.rfc3339;
        event = decoder.decode(line);
        Assert.assertEquals(1536057120000000000l / 1000000, event.getTimestamp().getMillis());
    }
}
