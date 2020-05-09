package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.*;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class KVFlowTest
 *
 * @author yundun-waf-dev
 * @date 2018-12-06
 */
public class KVFlowTest {

    @Test
    public void testCount() throws Exception {
        String     json  = IOUtils.resourceToString("sample.json", Charset.defaultCharset(), getClass().getClassLoader());// KVFlowTest.class.getClassLoader().getResourceAsStream("/sample.json");
        JSONObject o     = JSON.parseObject(json);
        TEvent     event = new DefaultEvent();
        event.setFields(o);
        KVFlowPrinterBuilder  pb  = new KVFlowPrinterBuilder();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        long                  now = System.currentTimeMillis();

        AtomicLong elementCounter = new AtomicLong();


        for (int i = 0; i < 10; i++) {
            TPrinter p = pb.create(bos);
            for (int j = 0; j < 1000; j++) {
                p.write(event);
                elementCounter.incrementAndGet();
            }
            p.flush();
        }
        System.out.println(System.currentTimeMillis() - now);

        KVFlowScannerBuilder sb      = new KVFlowScannerBuilder();
        AtomicLong           counter = new AtomicLong();
        byte[]               bs      = bos.toByteArray();

        System.out.println("bs'length = " + bs.length);

        now = System.currentTimeMillis();
        ByteArrayInputStream bis = new ByteArrayInputStream(bs);
        for (int i = 0; i < 10; i++) {
            TScanner s = sb.create(bis, new DefaultEventFactory());
            s.scan((e) -> {
                counter.incrementAndGet();
                return true;
            });
        }
        System.out.println(System.currentTimeMillis() - now);
        Assert.assertEquals(elementCounter.get(), counter.get());
    }

    @Test
    public void testEquals() throws Exception {
        String     json  = IOUtils.resourceToString("sample.json", Charset.defaultCharset(), getClass().getClassLoader());// KVFlowTest.class.getClassLoader().getResourceAsStream("/sample.json");
        JSONObject o     = JSON.parseObject(json);
        TEvent     event = new DefaultEvent();
        for (String k : o.keySet()) {
            event.setField(k, o.get(k));
        }
        KVFlowPrinterBuilder  pb  = new KVFlowPrinterBuilder();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TPrinter              p   = pb.create(bos);
        p.write(event);
        p.close();
        byte[]               bs      = bos.toByteArray();
        ByteArrayInputStream bis     = new ByteArrayInputStream(bs);
        KVFlowScannerBuilder sb      = new KVFlowScannerBuilder();
        TScanner             s       = sb.create(bis, new DefaultEventFactory());
        TEvent[]             wrapper = new TEvent[1];
        s.scan((e) -> {
            wrapper[0] = e;
            return true;
        });
        for (String key : o.keySet()) {
            Assert.assertEquals(o.get(key), wrapper[0].get(key));
        }
    }

    @Test
    public void testMany() throws Exception {
        Scanner br = new Scanner(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("1k.log")));
        br.useDelimiter("\n");
        List<TEvent> events = new ArrayList<>();
        while (br.hasNext()) {
            String line  = br.nextLine();
            TEvent event = new DefaultEvent();
            if (line.isEmpty()) {
                continue;
            }
            JSONObject o = JSON.parseObject(line);
            for (String k : o.keySet()) {
                event.setField(k, o.get(k));
            }
            events.add(event);
        }
        KVFlowPrinterBuilder  pb  = new KVFlowPrinterBuilder();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TPrinter              p   = pb.create(bos);
        for (TEvent event : events) {
            p.write(event);
        }
        p.close();
        byte[]               bs  = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(bs);
        KVFlowScannerBuilder sb  = new KVFlowScannerBuilder();
        TScanner             s   = sb.create(bis, new DefaultEventFactory());
        s.scan((e) -> {
            return true;
        });
    }
}
