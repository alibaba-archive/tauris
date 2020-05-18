package com.aliyun.tauris.plugins.scroll;

import com.aliyun.tauris.utils.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.function.BiConsumer;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class ScrollWriter implements Scroll {

    private static Logger LOG = LoggerFactory.getLogger(ScrollWriter.class);

    private String version;
    private String hostname;
    private String appName;
    private String token;

    private short headerLen;
    private byte[] header = new byte[MAX_HEADER_LEN];

    public ScrollWriter(String version, String hostname, String appName, String token) {
        this.version = version;
        this.hostname = hostname;
        this.appName = appName;
        this.token = token;
    }

    public void write(OutputStream out, byte[] bytes) throws IOException {

        out.write(MAGIC_NUMBER);
        out.write(new byte[]{1, 1});
        //write header
        headerLen = 0;
        header[headerLen++] = SCROLL_TIMESTAMP;
        MapperUtil.long2bytes(System.currentTimeMillis(), header, headerLen);
        headerLen += 8;

        BiConsumer<Byte, String> appendHeader = (t, s) -> {
            header[headerLen++] = SCROLL_GAP;
            header[headerLen++] = t;
            System.arraycopy(s.getBytes(), 0, header, headerLen, s.length());
            headerLen += s.getBytes().length;
        };
        appendHeader.accept(SCROLL_HOSTNAME, hostname);
        appendHeader.accept(SCROLL_APPNAME, appName);
        appendHeader.accept(SCROLL_TOKEN, token);
        appendHeader.accept(SCROLL_VERSION, version);

        byte[] b2 = new byte[2];
        MapperUtil.short2bytes(headerLen, b2, 0);
        out.write(b2);
        out.write(header, 0, headerLen);

        //write body
        byte[] b4 = new byte[4];
        MapperUtil.int2bytes(bytes.length, b4, 0);
        out.write(b4);
        out.write(bytes);
        out.flush();
    }
}
