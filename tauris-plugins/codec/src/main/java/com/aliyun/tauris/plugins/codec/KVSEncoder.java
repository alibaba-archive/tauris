package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

import java.io.*;
import java.util.Map;
import java.util.Set;


/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("kvs")
public class KVSEncoder extends AbstractEncoder {

    @Required
    char fieldSeperator;

    @Required
    char kvSeperator;

    @Required
    char delimiter;

    String[] fields;

    /**
     * 如果fields为空, 则不输出excludeFields
     */
    Set<String> excludeFields;

    /**
     * 如果field的value在ignoreValues中则不输出
     */
    Set<String> ignoreValues;

    /**
     * 如果value中包含与fieldSeperator或kvSeperator相同对字符，则自动将value做urlencode，同时在对应的key末尾加上'!'，表示此key的value是quote过的。
     */
    KVQuoteMode quoteMode = KVQuoteMode.none;

    @Override
    public void encode(TEvent event, String target) throws EncodeException {
        event.set(target, encode(event));
    }

    @Override
    public void encode(TEvent event, OutputStream output) throws EncodeException, IOException {
        writeEvent(event, output);
    }

    public boolean containsField(String fieldName, String fieldValue) {
        if (ignoreValues != null && ignoreValues.contains(fieldValue)) {
            return false;
        }
        if (excludeFields != null && excludeFields.contains(fieldName)) {
            return false;
        }
        return true;
    }

    protected void writeEvent(TEvent event, OutputStream output) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(output));
        if (fields == null) { //导出所有field
            Map<String, Object> fields = event.getFields();
            boolean first = true;
            for (Map.Entry<String, Object> e : fields.entrySet()) {
                String key = e.getKey();
                String val = e.getValue() == null ? null : e.getValue().toString();
                if (val != null && containsField(key, val)) {
                    if (!first) {
                        writer.write(fieldSeperator);
                    } else {
                        first = false;
                    }
                    writeValue(key, val, writer);
                }
            }
        } else {
            boolean first = true;
            for (String field: fields) {
                String key = field;
                Object val = null;
                if (field.contains(":")) {
                    String[] fs = field.split(":");
                    key = fs[0];
                    val = event.get(fs[1]);
                } else {
                    val = event.get(field);
                }
                String strval = val == null ? null : val.toString();
                if (strval != null && containsField(key, strval)) {
                    if (!first) {
                        output.write(fieldSeperator);
                    } else {
                        first = false;
                    }
                    writeValue(key, strval, writer);
                }
            }
        }
        writer.flush();
    }

    private void writeValue(String key, String val, Writer writer) throws IOException {
        writer.write(key);
        if (quoteMode != KVQuoteMode.none) {
            boolean needQuote = false;
            for (int i = 0; i < val.length(); i++) {
                char c = val.charAt(i);
                if (needQuote(c)) {
                    needQuote = true;
                    break;
                }
            }
            if (needQuote) {
                writer.write(quoteMode.markChar);
                writer.write(kvSeperator);
                writer.write(quoteMode.quote(val, charset));
            } else {
                writer.write(kvSeperator);
                writer.write(val);
            }
        } else {
            writer.write(kvSeperator);
            writer.write(val);
        }
        writer.flush();
    }

    private boolean needQuote(char c) {
        if (c == fieldSeperator || c == kvSeperator || c == delimiter) {
            return true;
        }
        return false;
    }
}
