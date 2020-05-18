package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by ZhangLei on 16/12/7.
 */
public class PlainEncoder extends AbstractEncoder {

    String source = TEvent.META_SOURCE;

    @Override
    public void encode(TEvent event, String target) throws EncodeException {
        event.set(target, event.get(source));
    }

    @Override
    public void encode(TEvent event, OutputStream output) throws EncodeException, IOException {
        writeEvent(event, output);
    }

    protected void writeEvent(TEvent event, OutputStream output) throws IOException {
        Object v = event.get(source);
        if (v == null) {
            return;
        }
        output.write(v.toString().getBytes(charset));
    }
}
