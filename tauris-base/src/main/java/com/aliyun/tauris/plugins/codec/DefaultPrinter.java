package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class DefaultPrinter
 *
 * @author yundun-waf-dev
 * @date 2018-11-16
 */
public class DefaultPrinter implements TPrinter {

    protected String delimiter = "\n";
    protected OutputStream writer;
    protected int bufferSize = 0;

    private TEncoder codec = new PlainEncoder();
    private byte[] delimiterBytes;

    public DefaultPrinter() {
    }

    public DefaultPrinter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public TPrinter wrap(OutputStream out) {
        DefaultPrinter printer = new DefaultPrinter(delimiter);
        if (bufferSize > 0) {
            printer.writer = new BufferedOutputStream(out, bufferSize);
        } else {
            printer.writer = out;
        }
        printer.codec = codec;
        return printer;
    }

    @Override
    public TPrinter withCodec(TEncoder codec) {
        this.codec = codec;
        return this;
    }

    @Override
    public void write(TEvent event) throws IOException, EncodeException {
        if (this.codec == null) {
            throw new IllegalStateException("codec is null");
        }
        codec.encode(event, writer);
        if (delimiterBytes == null) {
            delimiterBytes = delimiter.getBytes();
        }
        writer.write(delimiterBytes);
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
