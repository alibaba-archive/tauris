package com.aliyun.tauris.plugins.scroll;

import com.aliyun.tauris.*;
import com.aliyun.tauris.TScanner;
import com.aliyun.tauris.utils.MapperUtil;
import com.aliyun.tauris.utils.TLogger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by ZhangLei on 16/10/22.
 */
public class ScrollReader implements Scroll {

    private TLogger logger;

    private TQueue<List<TEvent>> queue;

    private TScanner scanner;
    private TDecoder decoder;

    private byte[] headerBuffer = new byte[MAX_HEADER_LEN];

    private String hostname;
    private String appName;
    private String token;
    private String version;

    public ScrollReader(TQueue<List<TEvent>> queue,
                        TScanner scanner,
                        TDecoder decoder) {
        this.logger = TLogger.getLogger(this);
        this.queue = queue;
        this.scanner = scanner;
        this.decoder = decoder;
    }

    public int read(InputStream input) throws IOException {
        if (readMagic(input) && readHeader(input)) {
            return readBody(input);
        }
        return -1;
    }

    boolean readMagic(InputStream in) throws IOException {
        byte[] bytes = new byte[2];
        if (IOUtils.read(in, bytes, 0, 2) != 2) {
            logger.debug("read magic number wrong bytes len");
            return false;
        }
        boolean r = (bytes[0] == MAGIC_NUMBER[0]) && (bytes[1] == MAGIC_NUMBER[1]);
        if (!r) {
            logger.warn(String.format("invalid magic number:[%x%x]", bytes[0], bytes[1]));
        }
        return r;
    }

    boolean readHeader(InputStream in) throws IOException {
        IOUtils.skip(in, 2);
        short headerLen = readShort(in);
        if (headerLen > MAX_HEADER_LEN) {
            logger.error("ERROR:header length too long");
            return false;
        }
        if (IOUtils.read(in, headerBuffer, 0, headerLen) != headerLen) {
            logger.error("ERROR: the header read length wrong");
            return false;
        }
        BiFunction<Byte[], Integer, Integer> findNextGap = (bytes, offset) -> {
            for (int i = offset; i < bytes.length; i++) {
                if (bytes[i] == SCROLL_GAP) {
                    return i;
                }
            }
            return -1;
        };
        try {
            byte[] bs = headerBuffer;
            for (int i = 10; i < headerLen; i++) {
                byte type = bs[i];
                if (type == SCROLL_TIMESTAMP) { //19
                    i += 9; // 8位时间戳 1位GAP
                    continue;
                }
                i++; // skip type
                int nextPos = findNextGap.apply(ArrayUtils.toObject(bs), i);
                if (nextPos == -1) {
                    break;
                }
                int len = nextPos - i;
                switch (type) {
                    case SCROLL_VERSION: //16
                        version = new String(bs, i, len);
                        break;
                    case SCROLL_APPNAME: //18
                        appName = new String(bs, i, len);
                        break;
                    case SCROLL_TOKEN:   //20
                        token = new String(bs, i, len);
                        break;
                    case SCROLL_HOSTNAME: //17
                        hostname = new String(bs, i, len);
                        break;
                }
                i = nextPos;
            }
        } catch (RuntimeException e) {
            logger.error("read scroll header error", e);
        }
        return true;
    }

    int readBody(InputStream in) throws IOException {
        int bodyLen = readInt(in);
        if (bodyLen == 0) {
            logger.WARN("empty body");
            return 0;
        }
        if (bodyLen > MAX_BODY_LEN * 2) {
            logger.WARN("body too large");
            IOUtils.skip(in, bodyLen);
            return 0;
        }
        InputStream reader = new BoundedInputStream(in, bodyLen);

        TScanner scanner = this.scanner.wrap(reader).withCodec(decoder);
        try {
            List<TEvent> events = new ArrayList<>(100);
            scanner.scan((event) -> {
                if (hostname != null) {
                    event.set("@hostname", hostname);
                    event.set("@app_name", appName);
                    event.set("@token", token);
                    event.set("@version", version);
                }
                events.add(event);
                return true;
            });
            if (!events.isEmpty()) {
                queue.put(events, events.size());
            }
        } catch (DecodeException e) {
            logger.WARN2("decode error", e, e.getSource());
        } catch (InterruptedException e) {
            logger.ERROR(e);
        }

        return bodyLen;
    }

    private int readInt(InputStream in) throws IOException {
        byte[] bytes = new byte[4];
        if (IOUtils.read(in, bytes, 0, 4) != 4) {
            throw new EOFException("ri");
        }
        return MapperUtil.bytes2int(bytes, 0);
    }

    private short readShort(InputStream in) throws IOException {
        byte[] bytes = new byte[2];
        if (IOUtils.read(in, bytes, 0, 2) != 2) {
            throw new EOFException("rs");
        }
        return MapperUtil.bytes2short(bytes, 0);
    }
}
