package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("kvs")
public class KVSDecoder extends AbstractDecoder {

    @Required
    char fieldSeperator = ',';

    @Required
    char kvSeperator = '=';

    private ThreadLocal<char[]> bufLocal = new ThreadLocal<>();

    @Override
    public void decode(String source, TEvent event, @Nullable String target) throws DecodeException {
        Map<String, Object> data = new HashMap<>();
        decode(source, (k, v) -> {
            if (target == null) {
                event.setField(k, v);
            } else {
                data.put(k, v);
            }
        });
        if (target != null) {
            event.set(target, data);
        }
    }

    @Override
    public TEvent decode(String source) throws DecodeException {
        TEvent event = new TEvent(source);
        decode(source, event::set);
        return event;
    }

    private char[] ensureCapacity(char[] buf, int length) {
        if (buf.length < length) {
            return new char[length];
        }
        return buf;
    }

    private void decode(String source, BiConsumer<String, String> put) {
        String              key  = null;
        char[]              buf  = bufLocal.get();
        if (buf == null) {
            buf = new char[source.length()];
            bufLocal.set(buf);
        } else {
            buf = ensureCapacity(buf, source.length());
        }

        int pos = 0;
        KVQuoteMode quoteMode = KVQuoteMode.none;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == fieldSeperator) {
                if (key != null) {
                    if (quoteMode != KVQuoteMode.none) {
                        String val = quoteMode.unquote(new String(buf, 0, pos), charset);
                        put.accept(key, val);
                    } else {
                        put.accept(key, new String(buf, 0, pos));
                    }
                }
                key = null;
                pos = 0;
                quoteMode = KVQuoteMode.none;
                continue;
            }
            if (c == kvSeperator) {
                if (buf[pos - 1] == KVQuoteMode.escape.markChar) {
                    key = new String(buf, 0, pos - 1);
                    quoteMode = KVQuoteMode.escape;
                } else if (buf[pos - 1] == KVQuoteMode.base64.markChar) {
                    key = new String(buf, 0, pos - 1);
                    quoteMode = KVQuoteMode.base64;
                } else {
                    key = new String(buf, 0, pos);
                }
                pos = 0;
                continue;
            }
            buf[pos++] = c;
        }
        if (pos > 0 && key != null) {
            if (quoteMode != KVQuoteMode.none) {
                String val = quoteMode.unquote(new String(buf, 0, pos), charset);
                put.accept(key, val);
            } else {
                put.accept(key, new String(buf, 0, pos));
            }
        }
    }
}
