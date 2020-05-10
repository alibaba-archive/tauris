package com.aliyun.tauris.utils;

import java.io.*;
import java.net.Socket;

/**
 * Class IOUtils
 *
 * @author yundun-waf-dev
 * @date 2019-03-19
 */
public class IOUtils {

    public static final int EOF = -1;

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
            }
        }
    }

    public static InputStream copyInputStream(final InputStream input, final int length) throws IOException {
        byte[] buffer = new byte[length];
        readFully(input, buffer, 0, length);
        return new ByteArrayInputStream(buffer);
    }

    public static void readFully(final InputStream input, final byte[] buffer, final int offset, final int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        int remaining = length;
        while (remaining > 0) {
            final int location = length - remaining;
            final int count = input.read(buffer, offset + location, remaining);
            if (EOF == count) { // EOF
                break;
            }
            remaining -= count;
        }
        final int actual = length - remaining;
        if (actual != length) {
            throw new EOFException("Length to read: " + length + " actual: " + actual);
        }
    }
}
