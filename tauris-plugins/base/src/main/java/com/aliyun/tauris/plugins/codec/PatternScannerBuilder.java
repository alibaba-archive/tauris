package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Class DelimiterScanner
 *
 * @author yundun-waf-dev
 * @date 2018-07-23
 */
@Name("pattern")
public class PatternScannerBuilder implements TScannerBuilder {

    String delimiter;

    TDecoder codec = new PlainDecoder();

    public PatternScannerBuilder(String delimiter) {
        this.delimiter = delimiter;
    }

    public PatternScannerBuilder() {
    }

    @Override
    public TScanner create(InputStream in, TEventFactory factory) {
        return new PatternScanner(in, codec, factory);
    }

    public class PatternScanner extends AbstractScanner {

        private Scanner scanner;

        public PatternScanner(InputStream in, TDecoder codec, TEventFactory factory) {
            super(codec, factory);
            scanner = new Scanner(in);
            scanner.useDelimiter(delimiter);
        }

        @Override
        public TEvent next() throws IOException, DecodeException {
            return codec.decode(scanner.next(), factory);
        }

        @Override
        public boolean hasNext() {
            return scanner.hasNext();
        }

        @Override
        public void close() throws IOException {
            scanner.close();
        }
    }
}
