package com.aliyun.tauris.utils;

import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class MultiLineIterator implements Iterator<String> {

    private final byte[] buffer;
    private final int length;

    private Charset charset;

    private char delimeter = '\n';

    private int cursor = 0;
    private int nextPos = -1;


    public MultiLineIterator(byte[] buffer, int length) {
        this.buffer = buffer;
        this.length = length;
    }

    public MultiLineIterator(byte[] buffer, int length, Charset charset) {
        this.buffer = buffer;
        this.length = length;
        this.charset = charset;
    }


    public MultiLineIterator(byte[] buffer, int length, Charset charset, char delimeter) {
        this.buffer = buffer;
        this.length = length;
        this.charset = charset;
        this.delimeter = delimeter;
    }

    @Override
    public boolean hasNext() {
        return nextPos < length;
    }

    @Override
    public String next() {
        nextPos = length;
        int strlen = 0;
        for (int i = cursor; i < length; i++) {
            strlen++;
            if (buffer[i] == delimeter) {
                break;
            }
        }
        String next = charset == null ? new String(buffer, cursor, strlen) : new String(buffer, cursor, strlen, charset);
        nextPos = cursor + strlen;
        cursor = nextPos;
        return next;
    }

}
