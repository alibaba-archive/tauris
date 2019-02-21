package com.aliyun.tauris.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ZhangLei on 16/12/2.
 */
public interface BytesWriter {

    void write(byte[] buffer, int length) throws IOException;

    void write(InputStream input, int length) throws IOException;

}
