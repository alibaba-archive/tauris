package com.aliyun.tauris.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ZhangLei on 16/12/2.
 */
public class StreamBytesReader implements ByteReader {

    private InputStream input;

    public StreamBytesReader(InputStream input) {
        this.input = input;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return input.read(buffer);
    }
}
