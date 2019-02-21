package com.aliyun.tauris.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ZhangLei on 16/12/2.
 */
public class StreamBytesWriter implements BytesWriter {

    private OutputStream output;

    public StreamBytesWriter(OutputStream output) {
        this.output = output;
    }

    @Override
    public void write(byte[] buffer, int length) throws IOException {
        this.output.write(buffer, 0, length);
    }

    @Override
    public void write(InputStream input, int length) throws IOException {
        byte[] buffer = new byte[length];
        IOUtils.read(input, buffer, 0, length);
        output.write(buffer, 0, length);
    }
}
