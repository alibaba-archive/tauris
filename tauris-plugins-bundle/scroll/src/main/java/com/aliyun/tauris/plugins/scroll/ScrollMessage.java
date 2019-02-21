package com.aliyun.tauris.plugins.scroll;

/**
 * Created by ZhangLei on 2018/4/15.
 */
public class ScrollMessage {

    private ScrollHeader header;

    private final byte[] bytes;

    private final int count;

    public ScrollMessage(byte[] bytes, int count) {
        this.bytes = bytes;
        this.count = count;
    }

    public ScrollMessage(ScrollHeader header, byte[] bytes, int count) {
        this.header = header;
        this.bytes = bytes;
        this.count = count;
    }

    public ScrollHeader getHeader() {
        return header;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getCount() {
        return count;
    }

    public boolean isEmpty() {
        return bytes.length == 0;
    }

    public String toString() {
        return new String(bytes, 0, count);
    }
}
