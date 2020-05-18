package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEncoder;
import com.aliyun.tauris.TEvent;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Class AbstractEncoder
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public abstract class AbstractEncoder implements TEncoder {

    protected Charset charset = Charset.defaultCharset();

    @Override
    public String encode(TEvent event) throws EncodeException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            encode(event, out);
        } catch (IOException e){
        }
        return out.toString(charset);
    }
}
