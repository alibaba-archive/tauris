package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.DefaultEventFactory;
import com.aliyun.tauris.TScanner;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Class MatchlineScannerTest
 *
 * @author yundun-waf-dev
 * @date 2018-08-23
 */
public class MatchlineScannerTest {

    @Test
    public void testOnlyHead() throws Exception {
        String[] lines = new String[]{
            "[2018-08-23] first", "[2018-08-23] second message1\nsecond message2", "[2018-08-23] last"
        };
        String                  text    = String.join("\n", lines);
        MatchlineScannerBuilder scanner = new MatchlineScannerBuilder();
        scanner.head = Pattern.compile("^\\[\\d+-\\d+-\\d+] .*");
        scanner.codec = new PlainDecoder();
        TScanner s = scanner.create(new ByteArrayInputStream(text.getBytes()), new DefaultEventFactory());
        final AtomicInteger index = new AtomicInteger();
        s.scan((event) -> {
            Assert.assertEquals(lines[index.getAndIncrement()], event.getSource());
            return true;
        });
        s.close();
    }

    @Test
    public void testHeadAndTail() throws Exception {
        JSONObject jack = new JSONObject();
        jack.put("name", "Jack");
        jack.put("age", 28);
        JSONObject mary = new JSONObject();
        mary.put("name", "mary");
        mary.put("age", 27);

        String[] blocks = new String[]{
                JSON.toJSONString(jack, true),
                JSON.toJSONString(mary, true)
        };
        String                  text    = String.join("\n", blocks);
        MatchlineScannerBuilder scanner = new MatchlineScannerBuilder();
        scanner.head = Pattern.compile("\\{$");
        scanner.tail = Pattern.compile("\\}$");
        scanner.codec = new PlainDecoder();
        TScanner s = scanner.create(new ByteArrayInputStream(text.getBytes()), new DefaultEventFactory());
        final AtomicInteger index = new AtomicInteger(0);
        s.scan((event) -> {
            int idx = index.getAndIncrement();
            Assert.assertEquals(blocks[idx], event.getSource());
            return true;
        });
        s.close();
    }

    @Test
    public void testOnlyTail() throws Exception {
        String[] blocks = new String[]{
               "first block line1 \n first block line2 \n--",
               "second block line1\nsecond block line2\n second block line3\n--"
        };
        String                  text    = String.join("\n", blocks);
        MatchlineScannerBuilder scanner = new MatchlineScannerBuilder();
        scanner.tail = Pattern.compile("--");
        scanner.codec = new PlainDecoder();
        TScanner s = scanner.create(new ByteArrayInputStream(text.getBytes()), new DefaultEventFactory());
        final AtomicInteger index = new AtomicInteger();
        s.scan((event) -> {
            Assert.assertEquals(blocks[index.getAndIncrement()], event.getSource());
            return true;
        });
        s.close();
    }
}
