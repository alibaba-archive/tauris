package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.utils.MapperUtil;

import javax.annotation.Nullable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("klv")
public class KLVDecoder extends AbstractDecoder {

    int headerLen = 16;

    boolean base64 = true;

    @Override
    public void decode(String source, TEvent event, @Nullable String target) throws DecodeException {
        byte[] data;
        if (base64) {
            data = Base64.getDecoder().decode(source);
        } else {
            data = source.getBytes(charset);
        }
        if (data.length < headerLen) {
            throw new DecodeException("invalid header");
        }
        Map<String, Object> kv = new HashMap<>();
        for (int cursor = headerLen; cursor < data.length;) {
            if (cursor + 4 > data.length) {
                throw new DecodeException("klv decode error, log has been truncated");
            }
            int keyLen = MapperUtil.lbytes2int(data, cursor);
            cursor += 4;
            if (cursor + keyLen > data.length) {
                throw new DecodeException(String.format("klv decode error, key length %d out of range %d", keyLen, data.length));
            }
            String key = new String(data, cursor, keyLen, charset);
            cursor += keyLen;
            if (cursor + 4 > data.length) {
                throw new DecodeException("klv decode error, log has been truncated");
            }
            int valLen = MapperUtil.lbytes2int(data, cursor);
            cursor += 4;
            if (cursor + valLen > data.length) {
                throw new DecodeException(String.format("klv decode error, value length %d out of range %d", valLen, data.length));
            }
            String val = new String(data, cursor, valLen, charset);
            cursor += valLen;
            if (target == null) {
                event.setField(key, val);
            } else {
                kv.put(key, val);
            }
        }
        if (target != null) {
            event.set(target, kv);
        }
    }

    @Override
    public TEvent decode(String source) throws DecodeException {
        TEvent event = new TEvent(source);
        decode(source, event::set);
        return event;
    }

    private void decode(String source, BiConsumer<String, String> put) throws DecodeException {
        byte[] data = Base64.getDecoder().decode(source);
        if (data.length < headerLen) {
            throw new DecodeException("invalid header");
        }
        for (int cursor = headerLen; cursor < data.length;) {
            int keyLen = MapperUtil.lbytes2int(data, cursor);
            cursor += 4;
            String key = new String(data, cursor, keyLen, charset);
            cursor += keyLen;
            int valLen = MapperUtil.lbytes2int(data, cursor);
            cursor += 4;
            String val = new String(data, cursor, valLen, charset);
            cursor += valLen;
            put.accept(key, val);
        }
    }
}
