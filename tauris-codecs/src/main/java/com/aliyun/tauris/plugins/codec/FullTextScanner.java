package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TDecoder;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TScanner;
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
public class FullTextScanner extends AbstractScanner {

    private Reader reader;

    private volatile boolean eof = false;

    private final Object lock = new Object();

    public FullTextScanner() {
    }

    public FullTextScanner(TDecoder codec,  Reader in) {
        super(codec);
        this.reader = new BufferedReader(in);
    }

    public TScanner wrap(InputStream in) {
        return new FullTextScanner(codec, new InputStreamReader(in));
    }

    public TEvent next() throws IOException, DecodeException {
        if (eof) {
            return null;
        }
        synchronized (lock) {
            eof = true;
            return codec.decode(IOUtils.toString(reader));
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
