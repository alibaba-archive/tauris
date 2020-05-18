package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TEventFormatter;
import com.aliyun.tauris.TFormatter;
import com.aliyun.tauris.annotations.Required;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ZhangLei on 16/12/7.
 */
public class FormatEncoder extends AbstractEncoder {

    @Required
    TFormatter formatter;

    @Override
    public void encode(TEvent event, String target) throws EncodeException {
        String text = formatter.format(event);
        event.set(target, text);
    }

    @Override
    public void encode(TEvent event, OutputStream output) throws EncodeException, IOException {
        output.write(formatter.format(event).getBytes(charset));
    }
}
