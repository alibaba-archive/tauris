package com.aliyun.tauris.utils;

import com.aliyun.tauris.annotations.Name;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Class FullTextReader
 *
 * @author yundun-waf-dev
 * @date 2018-06-07
 */
@Name("full")
public class FullTextReader implements BlockTextReader {

    Charset charset = Charset.defaultCharset();

    private Reader reader;

    private volatile boolean eof = false;

    private final Object lock = new Object();

    public FullTextReader() {
    }

    public FullTextReader(InputStream in) {
        this(new BufferedReader(new InputStreamReader(in, Charset.defaultCharset())));
    }

    public FullTextReader(Reader reader) {
        this(new BufferedReader(reader));
    }

    public FullTextReader(BufferedReader reader) {
        this.reader = reader;
    }

    public BlockTextReader wrap(InputStream in) {
        return new FullTextReader(new BufferedReader(new InputStreamReader(in, charset)));
    }

    public String read() throws IOException {
        if (eof) {
            return null;
        }
        synchronized (lock) {
            eof = true;
            return IOUtils.toString(reader);
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
