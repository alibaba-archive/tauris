package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Class KVCodecTest
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class MsgPackCodecTest {

    @Test
    public void test() throws Exception {
        MsgPackEncoder encoder = new MsgPackEncoder();
        TEvent event1 = new DefaultEvent();
        event1.set("pre", "k");
        event1.set("k1", "11");
        event1.set("k2", "12\n");
        event1.set("k3", "13");
        event1.set("k4", "14\n");

        TEvent event2 = new DefaultEvent();
        event2.set("pre", "j");
        event2.set("j1", "21");
        event2.set("j2", "22");
        event2.set("j3", "23");
        event2.set("j4", "24");

        TEvent[] events = new TEvent[] { event1, event2};


        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        encoder.encode(event1, writer);
        encoder.encode(event2, writer);
        writer.flush();


        MsgPackScanner scanner = (MsgPackScanner)(new MsgPackScanner().wrap(new ByteArrayInputStream(writer.toByteArray())));

        int i = 0;
        while (scanner.hasNext()) {
            TEvent e = scanner.next();
            String pre = (String)e.get("pre");
            Assert.assertEquals(events[i].get(pre + "1"), e.get(pre + "1"));
            Assert.assertEquals(events[i].get(pre + "2"), e.get(pre + "2"));
            Assert.assertEquals(events[i].get(pre + "3"), e.get(pre + "3"));
            Assert.assertEquals(events[i].get(pre + "4"), e.get(pre + "4"));
            i++;
        }
        Assert.assertEquals(events.length, i);
    }
}
