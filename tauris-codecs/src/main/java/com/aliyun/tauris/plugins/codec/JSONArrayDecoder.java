package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TEventFactory;
import com.aliyun.tauris.annotations.Name;

/**
 * 将字符串encode为json array对象
 * Created by ZhangLei on 16/12/7.
 */
@Name("json_array")
public class JSONArrayDecoder extends AbstractJSONDecoder {

    @Override
    public TEvent decode(String source, TEventFactory factory) throws DecodeException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(String source, TEvent event, String target) throws DecodeException {
        if (source == null || source.trim().isEmpty()) {
            throw new DecodeException("invalid json string, source is empty");
        }
        if (target == null) {
            throw new DecodeException("target must be set");
        }
        JSONArray result = parse(source);
        event.set(target, result);
    }

    private JSONArray parse(String text) throws DecodeException{
        try {
            return JSON.parseArray(text);
        } catch (JSONException e) {
            throw new DecodeException("json decode error", e, text);
        }
    }
}

