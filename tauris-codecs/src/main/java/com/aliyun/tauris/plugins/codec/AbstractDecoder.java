package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;

import java.nio.charset.Charset;

/**
 * Class AbstractDecoder
 *
 * @author yundun-waf-dev
 * @date 2018-06-06
 */
public abstract class AbstractDecoder implements TDecoder {

    protected Charset charset = Charset.defaultCharset();

    @Override
    public TEvent decode(byte[] source) throws DecodeException {
        return decode(new String(source, charset));
    }
}
