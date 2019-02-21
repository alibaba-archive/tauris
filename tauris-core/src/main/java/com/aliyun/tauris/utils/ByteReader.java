package com.aliyun.tauris.utils;

import java.io.IOException;

/**
 * Created by ZhangLei on 16/12/2.
 */
public interface ByteReader {

    int read(byte[] buffer) throws IOException;

}
