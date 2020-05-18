package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class ODPSOutputTest {


    @Test
    public void test() throws Exception {
        ODPSOutput out = new ODPSOutput();
        out.endPoint = "http://sg-service-maxcompute.aliyun-inc.com/api";
        out.accessKeyId = "a";
        out.accessKeySecret = "b";
        out.project = "c";
        out.tablename = "d";
        out.fields = new String[] {"index", "action", "name:username"};
        out.init();
        TEvent event = new DefaultEvent("");
        event.setField("index", "one");
        event.setField("action", "hello");
        event.setField("name", "world");
//        out.batchWrite(Collections.singletonList(event));
        out.stop();
    }
}
