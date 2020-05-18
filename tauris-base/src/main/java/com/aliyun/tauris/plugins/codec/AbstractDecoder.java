package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;

import java.nio.charset.Charset;

/**
 * Class AbstractDecoder
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public abstract class AbstractDecoder implements TDecoder {

    protected Charset charset = Charset.defaultCharset();

    @Override
    public TEvent decode(byte[] source, TEventFactory factory) throws DecodeException {
        return decode(new String(source, charset), factory);
    }
}
