package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.*;
import com.aliyun.tauris.plugins.codec.FullTextScannerBuilder;
import com.aliyun.tauris.plugins.codec.PlainDecoder;
import com.aliyun.tauris.plugins.codec.PlainEncoder;
import com.aliyun.tauris.TScanner;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.zip.*;

/**
 * Class CompressTest
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class CompressTest {

    String text = "cmVkPUV4ZWN1dGUoIkV4ZWN1dGUoIiJPbiBFcnJvciBSZXN1bWUgTmV4dDpSZXNwb25zZS5DbGVhcjpGdW5jdGlvbiBiZChieVZhbCBzKTpGb3IgaT0xIFRvIExlbihzKSBTdGVwIDI6Yz1NaWQocyxpLDIpOklmIElzTnVtZXJpYyhNaWQocyxpLDEpKSBUaGVuOkV4ZWN1dGUoIiIiImJkPWJkJmNociUoJkgiIiIiJmMmIiIiIikiIiIiKTpFbHNlOkV4ZWN1dGUoIiIiImJkPWJkJmNociUoJkgiIiIiJmMmTWlkKHMsaSsyLDIpJiIiIiIpIiIiIik6aT1pKzI6RW5kIElmIiImY2hyJSgxMCkmIiJOZXh0OkVuZCBGdW5jdGlvbjpSZXNwb25zZS5Xcml0ZSgiIiIiLT58IiIiIik6RXhlY3V0ZSgiIiIiT24gRXJyb3IgUmVzdW1lIE5leHQ6IiIiIiZiZCgiIiIiNDQ2OTZEMjA1MzNBNTMzRDUzNjU3Mjc2NjU3MjJFNEQ2MTcwNzA2MTc0NjgyODIyMkUyMjI5MjY2MzY4NzIyODM5MjkzQTUzNDU1NDIwNDMzRDQzNzI2NTYxNzQ2NTRGNjI2QTY1NjM3NDI4MjI1MzYzNzI2OTcwNzQ2OTZFNjcyRTQ2Njk2QzY1NTM3OTc";

    private void test(TEncoder encoder, TDecoder decoder, OutputStream writer, ReaderFactory readerFactory) throws Exception {
        TEvent event = new DefaultEvent(text);
        encoder.encode(event, writer);
        writer.close();

        InputStream reader  = readerFactory.newReader();
        TScanner    scanner = new FullTextScannerBuilder().create(reader, new DefaultEventFactory());
        scanner.scan((e) -> {
            Assert.assertEquals(text, e.getSource());
            return true;
        });
        reader.close();
    }

    @Test
    public void testNone() throws Exception {
        ByteArrayOutputStream bos     = new ByteArrayOutputStream();
        TEncoder              encoder = new PlainEncoder();
        OutputStream          writer  = bos;
        TDecoder              decoder = new PlainDecoder();
        test(encoder, decoder, writer, () -> new ByteArrayInputStream(bos.toByteArray()));
    }

    @Test
    public void testGzip() throws Exception {
        ByteArrayOutputStream bos     = new ByteArrayOutputStream();
        TEncoder         encoder = new PlainEncoder();
        OutputStream     writer  = new GZIPOutputStream(bos);
        TDecoder         decoder = new PlainDecoder();
        test(encoder, decoder, writer, () -> new GZIPInputStream(new ByteArrayInputStream(bos.toByteArray())));
    }


    @Test
    public void testLz4() throws Exception {
        ByteArrayOutputStream bos     = new ByteArrayOutputStream();
        TEncoder         encoder = new PlainEncoder();
        OutputStream     writer  = new GZIPOutputStream(bos);
        TDecoder         decoder = new PlainDecoder();
        test(encoder, decoder, writer, () -> new GZIPInputStream(new ByteArrayInputStream(bos.toByteArray())));
    }

    @Test
    public void testDeflate() throws Exception {
        ByteArrayOutputStream bos     = new ByteArrayOutputStream();
        TEncoder         encoder = new PlainEncoder();
        OutputStream     writer  = new DeflaterOutputStream(bos);
        TDecoder         decoder = new PlainDecoder();
        test(encoder, decoder, writer, () -> new InflaterInputStream(new ByteArrayInputStream(bos.toByteArray())));
    }

    interface ReaderFactory {
        InputStream newReader() throws IOException;
    }
}
