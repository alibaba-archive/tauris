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
public class KVSCodecTest {

    public void test(char fs, char kvs, KVQuoteMode quoteMode) throws Exception {
        KVSEncoder encoder = new KVSEncoder();
        encoder.fieldSeperator = fs;
        encoder.kvSeperator = kvs;
        encoder.delimiter = '\n';
        encoder.quoteMode = quoteMode;
        TEvent event1 = new TEvent();
        event1.set("pre", "k");
        event1.set("k1", "11");
        event1.set("k2", "12\n");
        event1.set("k3", "13");
        event1.set("k4", "14\n");

        TEvent event2 = new TEvent();
        event2.set("pre", "j");
        event2.set("j1", "21");
        event2.set("j2", "22");
        event2.set("j3", "23");
        event2.set("j4", "24");

        TEvent[] events = new TEvent[] { event1, event2};


        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        encoder.encode(event1, writer);
        writer.write('\n');
        encoder.encode(event2, writer);
        writer.flush();

        String encoded = writer.toString();

        KVSDecoder decoder = new KVSDecoder();
        decoder.fieldSeperator = fs;
        decoder.kvSeperator = kvs;


        String[] lines = encoded.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().isEmpty()) {
                continue;
            }
            TEvent e = decoder.decode(line);
            String pre = (String)e.get("pre");
            Assert.assertEquals(events[i].get(pre + "1"), e.get(pre + "1"));
            Assert.assertEquals(events[i].get(pre + "2"), e.get(pre + "2"));
            Assert.assertEquals(events[i].get(pre + "3"), e.get(pre + "3"));
            Assert.assertEquals(events[i].get(pre + "4"), e.get(pre + "4"));
        }
    }

    @Test
    public void test() throws Exception {
        test('&', '=', KVQuoteMode.base64);
        test('\1', '\2', KVQuoteMode.base64);
        test('&', '=', KVQuoteMode.escape);
        test('\1', '\2', KVQuoteMode.escape);
    }

    @Test
    public void unvisibleChar() throws Exception {
        KVSEncoder encoder = new KVSEncoder();
        encoder.fieldSeperator = '\0';
        encoder.kvSeperator = '\1';
        encoder.quoteMode = KVQuoteMode.base64;

        TEvent event = new TEvent();
        event.set("pre", "k\1");
        event.set("k1", "11\2");
        event.set("k2", "12\3");
        event.set("k3", "13\4");
        event.set("k4", "14\5");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        encoder.encode(event, output);

        String encoded = output.toString();

        KVSDecoder decoder = new KVSDecoder();
        decoder.fieldSeperator = '\0';
        decoder.kvSeperator = '\1';

        TEvent event2 = decoder.decode(encoded);

        for (String k : event.getFields().keySet()) {
            Assert.assertEquals(event.get(k), event2.get(k));
        }
    }
}
