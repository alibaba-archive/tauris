package com.aliyun.tauris.utils;

import com.aliyun.tauris.annotations.Name;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Class MultiLineReader
 *
 * @author yundun-waf-dev
 * @date 2018-06-07
 */
@Name("multiline")
public class MultiLineReader implements BlockTextReader {

    private BufferedReader reader;

    private char[] buf = new char[32];
    private int    off = 0;

    private volatile boolean eof = false;

    private final Object lock = new Object();

    public MultiLineReader() {
    }

    public MultiLineReader(InputStream in) {
        this(new BufferedReader(new InputStreamReader(in, Charset.defaultCharset())));
    }

    public MultiLineReader(Reader reader) {
        this(new BufferedReader(reader));
    }

    public MultiLineReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public BlockTextReader wrap(InputStream in) {
        return new MultiLineReader(new BufferedReader(new InputStreamReader(in)));
    }

    public String read() throws IOException {
        if (eof) {
            return null;
        }
        synchronized (lock) {
            String current;
            while (true) {
                current = reader.readLine();
                if (current == null) {
                    eof = true;
                    return off > 0 ? new String(buf, 0, off) : null;
                }
                if (isLinetail(current)) {
                    fill(current);
                    String ret = new String(buf, 0, off);
                    off = 0;
                    return ret;
                }
                if (isLinehead(current) && off > 0) {
                    String ret = new String(buf, 0, off);
                    off = 0;
                    fill(current);
                    return ret;
                }
                fill(current);
            }
        }
    }

    private void fill(String line) {
        char[] chars = line.toCharArray();
        ensureCapacity(off + chars.length);
        System.arraycopy(chars, 0, buf, off, chars.length);
        off += chars.length;
    }

    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buf.length > 0) {
            grow(minCapacity);
        }
    }

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        if (newCapacity < 0) {
            // overflow
            if (minCapacity < 0) {
                throw new OutOfMemoryError();
            }
            newCapacity = Integer.MAX_VALUE;
        }
        buf = Arrays.copyOf(buf, newCapacity);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    protected boolean isLinehead(String line) {
        return true;
    }

    protected boolean isLinetail(String line) {
        return false;
    }
}
