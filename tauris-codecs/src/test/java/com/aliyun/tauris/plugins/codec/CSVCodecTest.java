package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.TEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

/**
 * Class KVCodecTest
 *
 * @author zhanglei
 * @date 2018-09-05
 */
public class CSVCodecTest {

    public void test(String[] fields, char seperator, Character quoteChar) throws Exception {
        CSVEncoder encoder = new CSVEncoder();
        encoder.separator = seperator;
        encoder.fields = fields;
        encoder.quotechar = quoteChar;
        encoder.init();


        TEvent event1 = new TEvent();
        event1.set("pre", "k");
        event1.set("k1", "11");
        event1.set("k2", "1\t2");
        event1.set("k3", "1\f3");
        event1.set("k4", "14");

        TEvent event2 = new TEvent();
        event2.set("pre", "k");
        event2.set("k1", "1\b2");
        event2.set("k2", "2\b2");
        event2.set("k3", "23");
        event2.set("k4", "24");

        TEvent[] events = new TEvent[] { event1, event2};


        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        encoder.encode(event1, writer);
        writer.write('\n');
        encoder.encode(event2, writer);
        writer.flush();

        String encoded = writer.toString();

        CSVDecoder decoder = new CSVDecoder();
        decoder.fields = fields;
        decoder.separator = seperator;
        decoder.quotechar = quoteChar;
        decoder.init();


        String[] lines = encoded.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().isEmpty()) {
                continue;
            }
            TEvent e = decoder.decode(line);
            String pre = (String)e.get("pre");
            Assert.assertNotNull(e.get(pre + "1"));
            Assert.assertEquals(events[i].get(pre + "1"), e.get(pre + "1"));
            Assert.assertEquals(events[i].get(pre + "2"), e.get(pre + "2"));
            Assert.assertEquals(events[i].get(pre + "3"), e.get(pre + "3"));
            Assert.assertEquals(events[i].get(pre + "4"), e.get(pre + "4"));
        }
    }

    @Test
    public void test() throws Exception {
        String[] fields = new String[] {
                "pre", "k1", "k2", "k3", "k4"
        };
        test(fields, '&', '"');
        test(fields, '\1', '"');
        test(fields, '&', '"');
        test(fields, '\1', '"');
        test(fields, '\1', '"');
    }
}
