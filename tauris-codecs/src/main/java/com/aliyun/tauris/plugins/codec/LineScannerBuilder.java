package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.TScannerBuilder;
import com.aliyun.tauris.annotations.Name;

import java.io.*;
import java.util.Scanner;

/**
 * Class LineScanner
 *
 * @author yundun-waf-dev
 * @date 2018-06-07
 */
@Name("line")
public class LineScannerBuilder implements TScannerBuilder {

    boolean skipBlank = true;

    TDecoder codec = new PlainDecoder();

    public LineScannerBuilder() {
    }

    @Override
    public TScanner create(InputStream in, TEventFactory factory) {
        return new LineScanner(in, codec, factory);
    }

    public static class LineScanner extends AbstractScanner {

        private Scanner scanner;

        public LineScanner(InputStream input, TDecoder codec, TEventFactory factory) {
            super(codec, factory);
            this.scanner = new Scanner(input);
            this.scanner.useDelimiter("\n");
        }

        @Override
        public TEvent next() throws DecodeException {
            return codec.decode(this.scanner.nextLine(), factory);
        }

        @Override
        public boolean hasNext() {
            return this.scanner.hasNext();
        }

        @Override
        public void close() throws IOException {
            scanner.close();
        }
    }
}
