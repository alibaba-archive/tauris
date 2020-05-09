package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class DefaultPrinter
 *
 * @author yundun-waf-dev
 * @date 2018-11-16
 */
@Name("default")
public class DefaultPrinterBuilder extends EncodePrinterBuilder {

    protected String delimiter = "\n";
    protected int bufferSize = 0;

    private byte[] delimiterBytes;

    public DefaultPrinterBuilder() {
    }

    public DefaultPrinterBuilder(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public TPrinter create(OutputStream out) {
        if (codec == null) {
            throw new IllegalStateException("codec is required before create printer");
        }
        if (bufferSize > 0) {
            out = new BufferedOutputStream(out, bufferSize);
        }
        return new DefaultPrinter(out);
    }

    public class DefaultPrinter implements TPrinter {

        private OutputStream writer;

        public DefaultPrinter(OutputStream writer) {
            this.writer = writer;
        }

        @Override
        public synchronized void write(TEvent event) throws IOException, EncodeException {
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
}
