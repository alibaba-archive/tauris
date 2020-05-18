package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


/**
 * 将对象encode为字符串
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("jsoniter")
public class JsoniterEncoder extends AbstractEncoder {

    @Override
    public void encode(TEvent event, String target) throws EncodeException {
        Map<String, Object> fields = event.getFields();
        String json = JsonStream.serialize(fields);
        event.set(target, json);
    }

    @Override
    public void encode(TEvent event, OutputStream output) throws EncodeException, IOException {
        JsonStream.serialize(event.getFields(), output);
    }

}

