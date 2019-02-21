package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.formatter.TEventFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Created by ZhangLei on 16/12/7.
 */
public class FormatEncoder extends AbstractEncoder {

    @Required
    TEventFormatter formatter;

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
