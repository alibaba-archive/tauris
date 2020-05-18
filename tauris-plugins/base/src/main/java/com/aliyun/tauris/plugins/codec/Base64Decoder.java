package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TEventFactory;
import com.aliyun.tauris.annotations.Name;

import java.nio.charset.Charset;
import java.util.Base64;


/**
 * 将字符串encode为json对象
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("base64")
public class Base64Decoder extends AbstractDecoder {

    Charset charset = Charset.defaultCharset();

    @Override
    public TEvent decode(String source, TEventFactory factory) throws DecodeException {
        byte[] bs = Base64.getDecoder().decode(source);
        return factory.create(new String(bs, charset));
    }

    @Override
    public void decode(String source, TEvent event, String target) throws DecodeException {
        byte[] bs = Base64.getDecoder().decode(source);
        event.set(target, new String(bs, charset));
    }
}

