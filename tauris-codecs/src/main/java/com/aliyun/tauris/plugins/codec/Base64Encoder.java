package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;


/**
 * 将对象encode为字符串
 * Created by ZhangLei on 16/12/7.
 */
@Name("base64")
public class Base64Encoder extends AbstractEncoder {

    @Required
    String source;

    @Override
    public void encode(TEvent event, String target) throws EncodeException {
        Object value = event.get(source);
        if (value != null && value instanceof String) {
            event.set(target, Base64.getEncoder().encodeToString(((String) value).getBytes()));
        }
    }

    @Override
    public void encode(TEvent event, OutputStream output) throws EncodeException, IOException {
        Object value = event.get(source);
        if (value == null) {
            return;
        }
        if (!(value instanceof String)) {
            throw new EncodeException("value not a string type");
        }
        byte[] bs = Base64.getEncoder().encode(((String) value).getBytes());
        output.write(bs);
    }
}

