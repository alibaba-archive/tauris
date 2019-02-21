package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPrinter;
import com.aliyun.tauris.TScanner;
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

//    @Test
    public void testCount() throws Exception {
        String json = IOUtils.resourceToString("/sample.json", Charset.defaultCharset());// KVFlowTest.class.getClassLoader().getResourceAsStream("/sample.json");
        JSONObject o = JSON.parseObject(json);
        TEvent event = new TEvent();
        for (String k : o.keySet()) {
            event.setField(k, o.get(k));
        }
        KVFlowPrinter printer = new KVFlowPrinter();
        printer.init();
        ByteArrayOutputStream bos = null;
        long now = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            bos = new ByteArrayOutputStream();
            TPrinter p = printer.wrap(bos);
            for (int j = 0; j < 1000; j++) {
                p.write(event);
            }
            p.flush();
        }
        System.out.println(System.currentTimeMillis() - now);

        KVFlowScanner scanner = new KVFlowScanner();
        AtomicLong counter = new AtomicLong();
        byte[] bs = bos.toByteArray();

        now = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            ByteArrayInputStream bis = new ByteArrayInputStream(bs);
            TScanner s = scanner.wrap(bis);
            s.scan((e) -> {
                counter.incrementAndGet();
                return true;
            });
        }
        System.out.println(System.currentTimeMillis() - now);
        Assert.assertEquals(10 * 1000, counter.get());
    }

//    @Test
    public void testEquals() throws Exception {
        String json = IOUtils.resourceToString("/sample.json", Charset.defaultCharset());// KVFlowTest.class.getClassLoader().getResourceAsStream("/sample.json");
        JSONObject o = JSON.parseObject(json);
        TEvent event = new TEvent();
        for (String k : o.keySet()) {
            event.setField(k, o.get(k));
        }
        KVFlowPrinter printer = new KVFlowPrinter();
        printer.init();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TPrinter p = printer.wrap(bos);
        p.write(event);
        p.close();
        byte[] bs = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(bs);
        KVFlowScanner scanner = new KVFlowScanner();
        TScanner s = scanner.wrap(bis);
        TEvent[] wrapper = new TEvent[1];
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
        Scanner br = new Scanner(new InputStreamReader(KVFlowTest.class.getClassLoader().getResourceAsStream("1k.log")));
        br.useDelimiter("\n");
        List<TEvent> events = new ArrayList<>();
        while (br.hasNext()) {
            String line = br.nextLine();
            TEvent event = new TEvent();
            if (line.isEmpty()) {
                continue;
            }
            JSONObject o = JSON.parseObject(line);
            for (String k : o.keySet()) {
                event.setField(k, o.get(k));
            }
            events.add(event);
        }
        KVFlowPrinter printer = new KVFlowPrinter();
        printer.init();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TPrinter p = printer.wrap(bos);
        for (TEvent event: events) {
            p.write(event);
        }
        p.close();
        byte[] bs = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(bs);
        KVFlowScanner scanner = new KVFlowScanner();
        TScanner s = scanner.wrap(bis);
        s.scan((e) -> {
            return true;
        });
    }
}
