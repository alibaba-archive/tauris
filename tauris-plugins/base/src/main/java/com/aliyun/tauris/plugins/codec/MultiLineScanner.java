package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TDecoder;
import com.aliyun.tauris.TEvent;

import java.io.*;
import java.util.Arrays;

/**
 * Class MultiLineScanner
 *
 * @author yundun-waf-dev
 * @date 2018-06-07
 */
public abstract class MultiLineScanner extends AbstractScanner {

    private BufferedReader reader;

    private char[] buf = new char[32];
    private int    off = 0;

    private final Object lock = new Object();

    private boolean eof;

    private String line;

    public MultiLineScanner() {
    }

    public MultiLineScanner(TDecoder codec, Reader reader) {
        super(codec);
        this.reader = new BufferedReader(reader);
    }

    @Override
    public TEvent next() throws IOException, DecodeException {
        synchronized (lock) {
            if (line == null) {
                line = reader.readLine();
                if (line == null) {
                    if (off == 0) {
                        throw new EOFException();
                    } else {
                        eof = true;
                        String ret = new String(buf, 0, off);
                        off = 0;
                        return codec.decode(ret, factory);
                    }
                }
            }
            while (true) {
                if (isLinetail(line)) {
                    fill(line);
                    String ret = new String(buf, 0, off);
                    off = 0;
                    line = null;
                    return codec.decode(ret, factory);
                }
                if (isLinehead(line) && off > 0) {
                    String ret = new String(buf, 0, off);
                    off = 0;
                    fill(line);
                    line = null;
                    return codec.decode(ret, factory);
                }
                fill(line);
                line = reader.readLine();
                if (line == null) {
                    eof = true;
                    break;
                }
            }
            String ret = new String(buf, 0, off);
            off = 0;
            return codec.decode(ret, factory);
        }
    }

    public boolean hasNext() {
        return !eof || off > 0;
    }

    private void fill(String line) {
        char[] chars = line.toCharArray();
        if (off == 0) {
            ensureCapacity(off + chars.length);
            System.arraycopy(chars, 0, buf, off, chars.length);
            off += chars.length;
        } else {
            ensureCapacity(off + chars.length + 1);
            System.arraycopy(chars, 0, buf, off + 1, chars.length);
            buf[off] = '\n';
            off += chars.length + 1;
        }
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
