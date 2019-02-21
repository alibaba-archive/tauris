package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TDecoder;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TScanner;
import com.aliyun.tauris.annotations.Name;

import java.io.*;

/**
 * Class LineScanner
 *
 * @author yundun-waf-dev
 * @date 2018-06-07
 */
@Name("line")
public class LineScanner extends AbstractScanner {

    private BufferedReader reader;

    private String line;

    public LineScanner() {
    }

    public LineScanner(TDecoder codec, BufferedReader reader) {
        super(codec);
        this.reader = reader;
    }

    @Override
    public TScanner wrap(InputStream in) {
        return new LineScanner(codec,  new BufferedReader(new InputStreamReader(in)));
    }

    @Override
    public TEvent next() throws IOException, DecodeException {
        return codec.decode(line);
    }

    @Override
    public boolean hasNext() {
        try {
            this.line = reader.readLine();
            return line != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

}
