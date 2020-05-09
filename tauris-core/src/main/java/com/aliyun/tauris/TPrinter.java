package com.aliyun.tauris;

import java.io.Closeable;
import java.io.IOException;

/**
 * Class TPrinter
 *
 * @author yundun-waf-dev
 * @date 2018-07-23
 */
public interface TPrinter extends Closeable {

    void write(TEvent event) throws IOException, EncodeException;

    void flush() throws IOException;
}
