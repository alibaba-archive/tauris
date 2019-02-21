package com.aliyun.tauris.utils;

import com.aliyun.tauris.annotations.Name;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * Class SeperateReader
 *
 * @author yundun-waf-dev
 * @date 2018-06-07
 */
@Name("matchline")
public class MatchLineReader extends MultiLineReader {

    Charset charset;

    Pattern head;

    Pattern tail;

    public MatchLineReader() {

    }

    public MatchLineReader(InputStream in, Pattern head, Pattern tail) {
        super(in);
        this.head = head;
        this.tail = tail;
    }

    public MatchLineReader(Reader reader, Pattern head, Pattern tail) {
        super(reader);
        this.head = head;
        this.tail = tail;
    }

    public MatchLineReader(BufferedReader reader, Pattern head, Pattern tail) {
        super(reader);
        this.head = head;
        this.tail = tail;
    }

    @Override
    public BlockTextReader wrap(InputStream in) {
        return new MatchLineReader(new BufferedReader(new InputStreamReader(in, charset)), head, tail);
    }

    public BlockTextReader wrap(Reader reader) {
        return new MatchLineReader(reader, head, tail);
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
