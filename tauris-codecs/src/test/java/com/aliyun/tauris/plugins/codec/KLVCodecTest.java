package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.TEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Set;

/**
 * Class KVCodecTest
 *
 * @author chuanshi.zl<chuanshi.zl@alibaba-inc.com>
 * @date 2018-09-05
 */
public class KLVCodecTest {

    @Test
    public void test() throws Exception {
        String source = "0AcAAsIBAAAAAAAAMDAwMAwAAABtYXRjaGVkX2hvc3QPAAAAdHVsb25nLnRlc3QuY29tDgAAAHJlYWxfY2xpZW50X2lwCAAAADEuMi4zMy40BwAAAGNsdXN0ZXIOAAAAbS55dW5kdW4ud2FmLjEHAAAAdXNlcl9pZBAAAAAxNTE5NzE0MDQ5NjMyNzY0CQAAAGNjX2FjdGlvbgUAAAB0cmFjZQoAAABjY19ydWxlX2lkAAAAAAwAAABjY19ydWxlX3R5cGUAAAAABwAAAGNjX3Rlc3QFAAAAZmFsc2UOAAAAY2NfZGlzYWJsZV9sb2cEAAAAdHJ1ZQoAAAB3YWZfYWN0aW9uBQAAAGJsb2NrCwAAAHdhZl9ydWxlX2lkBgAAADkwMDAwNw0AAAB3YWZfcnVsZV90eXBlBAAAAHZ2aXAIAAAAd2FmX3Rlc3QFAAAAZmFsc2UMAAAAZmluYWxfcGx1Z2luAwAAAHdhZgwAAABmaW5hbF9hY3Rpb24FAAAAYmxvY2sNAAAAZmluYWxfcnVsZV9pZAYAAAA5MDAwMDcPAAAAZmluYWxfcnVsZV90eXBlBAAAAHZ2aXAKAAAAZmluYWxfdGVzdAUAAABmYWxzZQ";
        KLVDecoder decoder = new KLVDecoder();
        TEvent event = decoder.decode(source);

        decoder.decode(source, event, null);

        KLVEncoder encoder = new KLVEncoder();
        encoder.encode(event, "base64");
        String encoded = (String)event.get("base64");
        event.remove("base64");

        TEvent event2 = decoder.decode(encoded);

        Set<String> keys = event.getFields().keySet();
        for (String key: keys) {
            Assert.assertEquals(event.get(key), event2.get(key));

        }

    }

}
