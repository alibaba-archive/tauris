package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.TDecoder;
import com.aliyun.tauris.TScanner;
import com.aliyun.tauris.annotations.Name;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Pattern;

/**
 * Class DelimiterScanner
 *
 * @author yundun-waf-dev
 * @date 2018-07-23
 */
@Name("match")
public class MatchlineScanner extends MultiLineScanner {

    Pattern head;

    Pattern tail;

    public MatchlineScanner() {
    }

    public MatchlineScanner(TDecoder codec,  Reader in, Pattern head, Pattern tail) {
        super(codec, in);
        this.head = head;
        this.tail = tail;
    }

    @Override
    public TScanner wrap(InputStream in) {
        return new MatchlineScanner(codec, new InputStreamReader(in), head, tail);
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
