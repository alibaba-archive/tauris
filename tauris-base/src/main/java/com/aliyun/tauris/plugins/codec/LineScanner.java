package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
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

    boolean skipBlank = true;

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
        return codec.decode(line, factory);
    }

    @Override
    public boolean hasNext() {
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    return false;
                }
                if (skipBlank) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                }
                this.line = line;
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

}
