package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TScanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Class DelimiterScanner
 *
 * @author yundun-waf-dev
 * @date 2018-07-23
 */
public class PatternScanner extends AbstractScanner {

    String delimiter;

    private Scanner scanner;

    public PatternScanner(String delimiter) {
        this.delimiter = delimiter;
    }

    public PatternScanner() {
    }

    @Override
    public TScanner wrap(InputStream in) {
        PatternScanner scanner = new PatternScanner(delimiter);
        scanner.scanner = new Scanner(in);
        scanner.scanner.useDelimiter(delimiter);
        return scanner;
    }

    @Override
    public TEvent next() throws IOException, DecodeException {
        return codec.decode(scanner.next());
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
