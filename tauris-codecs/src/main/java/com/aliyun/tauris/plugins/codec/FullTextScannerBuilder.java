package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Class FullTextReader
 *
 * @author yundun-waf-dev
 * @date 2018-06-07
 */
@Name("full")
public class FullTextScannerBuilder implements TScannerBuilder {


    TDecoder codec = new PlainDecoder();

    public FullTextScannerBuilder() {
    }

    public FullTextScannerBuilder(TDecoder codec) {
        this.codec = codec;
    }

    @Override
    public TScanner create(InputStream in, TEventFactory factory) {
        return new FullTextScanner(in, codec, factory);
    }

    public static class FullTextScanner extends AbstractScanner {

        private Reader reader;

        private volatile boolean eof = false;

        private final Object lock = new Object();

        public FullTextScanner(InputStream in, TDecoder codec, TEventFactory factory) {
            super(codec, factory);
            this.reader = new InputStreamReader(in);
        }

        public TEvent next() throws IOException, DecodeException {
            if (eof) {
                return null;
            }
            synchronized (lock) {
                eof = true;
                return codec.decode(IOUtils.toString(reader), factory);
            }
        }

        @Override
        public boolean hasNext() {
            return !eof;
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }
}
