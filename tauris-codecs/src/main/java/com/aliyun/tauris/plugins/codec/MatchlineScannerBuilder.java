package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.TDecoder;
import com.aliyun.tauris.TEventFactory;
import com.aliyun.tauris.TScanner;
import com.aliyun.tauris.TScannerBuilder;
import com.aliyun.tauris.annotations.Name;

import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * Class DelimiterScanner
 *
 * @author yundun-waf-dev
 * @date 2018-07-23
 */
@Name("match")
public class MatchlineScannerBuilder extends MultiLineScannerBuilder {

    Pattern head;

    Pattern tail;

    TDecoder codec = new PlainDecoder();

    public MatchlineScannerBuilder() {
    }

    public MatchlineScannerBuilder(Pattern head, Pattern tail, TDecoder codec) {
        this.head = head;
        this.tail = tail;
        this.codec = codec;
    }

    @Override
    public TScanner create(InputStream in, TEventFactory factory) {
        return new MatchlineScanner(in, codec, factory);
    }

    public class MatchlineScanner extends MultiLineScannerBuilder.MultiLineScanner {

        public MatchlineScanner(InputStream in, TDecoder codec, TEventFactory factory) {
            super(in, codec, factory);
        }

        @Override
        protected boolean isLinehead(String line) {
            if (line == null) {
                return false;
            }
            return head != null && head.matcher(line).matches();
        }

        @Override
        protected boolean isLinetail(String line) {
            if (line == null) {
                return false;
            }
            return tail != null && tail.matcher(line).matches();
        }
    }
}
