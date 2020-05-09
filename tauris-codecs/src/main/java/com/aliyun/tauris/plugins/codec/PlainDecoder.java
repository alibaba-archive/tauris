package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TEventFactory;

/**
 * Created by ZhangLei on 16/12/7.
 */
public class PlainDecoder extends AbstractDecoder {

    boolean trim;

    @Override
    public void decode(String source, TEvent event, String target) throws DecodeException {
        event.set(target, trim ? source.trim() : source);
    }

    @Override
    public TEvent decode(String source, TEventFactory factory) throws DecodeException {
        return factory.create(source);
    }
}
