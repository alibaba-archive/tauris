package com.aliyun.tauris;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class TPrinter
 *
 * @author yundun-waf-dev
 * @date 2018-07-23
 */
public interface TPrinter extends TPlugin, Closeable {

    TPrinter wrap(OutputStream out);

    TPrinter withCodec(TEncoder codec);

    void write(TEvent event) throws IOException, EncodeException;

    void flush() throws IOException;
}
