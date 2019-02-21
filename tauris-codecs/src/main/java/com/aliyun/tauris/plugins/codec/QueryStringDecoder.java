package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * $source是url的querystring字符串, 将querystring解码成key-value格式
 * Created by ZhangLei on 17/10/13.
 */
@Name("querystring")
public class QueryStringDecoder extends AbstractDecoder {

    Set<String> includes;
    Set<String> excludes;

    boolean arrayValue = false;
    boolean overwrite  = false;
    boolean unquote    = true;


    @Override
    public TEvent decode(String source) throws DecodeException {
        TEvent event = new TEvent();
        decode(source, event, null);
        return null;
    }

    public void decode(String text, TEvent event) throws DecodeException {
        decode(text, event, null);
    }

    @Override
    public void decode(String source, TEvent event, @Nullable String target) throws DecodeException {
        if (arrayValue) {
            filterSupportArray(source, event, target);
        } else {
            filterSimple(source, event, target);
        }
    }

    private boolean filterSimple(String text, TEvent event, String target) {
        try {
            Map<String, Object> tgt = target == null ? event.getFields() : Maps.newHashMap();
            KeyValueIterator iter = new KeyValueIterator(text);
            while (iter.hasNext()) {
                KeyValue kv = iter.next();
                String k = kv.getKey();
                String v = kv.getValue();
                if (!include(k)) {
                    continue;
                }
                if (overwrite || !tgt.containsKey(k)) {
                    tgt.put(k, unquote(v));
                }
            }
            if (target != null) {
                event.set(target, tgt);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean filterSupportArray(String text, TEvent event, String target) {
        try {
            Map<String, Object> tgt = target == null ? event.getFields() : Maps.newHashMap();
            KeyValueIterator iter = new KeyValueIterator(text);
            while (iter.hasNext()) {
                KeyValue kv = iter.next();
                String k = kv.getKey();
                if (!include(k)) {
                    continue;
                }
                if (tgt.containsKey(k)) {
                    Object o = tgt.get(k);
                    if (o instanceof List) {
                        ((List)o).add(unquote(kv.getValue()));
                    } else {
                        List<Object> os = Lists.newLinkedList();
                        os.add(o);
                        os.add(unquote(kv.getValue()));
                        tgt.put(k, os);
                    }
                } else {
                    tgt.put(k, unquote(kv.getValue()));
                }
            }
            if (target != null) {
                event.setField(target, tgt);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean include(String key) {
        if (includes == null && excludes == null) {
            return true;
        }
        if (excludes != null && excludes.contains(key)) {
            return false;
        }
        if (includes != null) {
            return includes.contains(key);
        }
        return true;
    }

    private String unquote(String value) throws DecoderException, UnsupportedEncodingException {
        return unquote ? new String(URLCodec.decodeUrl(value.getBytes()), "UTF-8") : value;
    }

    public static class KeyValue {

        private String pair;
        private int c;
        public KeyValue(String pair) {
            this.pair = pair;
            this.c = pair.indexOf('=');
        }

        public String getKey() {
            return c < 0 ? pair : pair.substring(0, c);
        }

        public String getValue() {
            return c > 0 ? pair.substring(c + 1) : "";
        }

        public String toString() {
            return pair;
        }
    }

    public static class KeyValueIterator implements Iterator<KeyValue> {

        private String queryString;

        private int start = 0;
        private int cursor;

        public KeyValueIterator(String queryString) {
            this.queryString = queryString;
        }

        @Override
        public boolean hasNext() {
            cursor = queryString.indexOf('&', start + 1);
            return start < queryString.length();
        }

        @Override
        public KeyValue next() {
            int end = cursor < 0 ? queryString.length() : cursor;
            KeyValue v = new KeyValue(queryString.substring(start, end));
            start = end + 1;
            return v;
        }
    }
}

