package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("msgpack")
public class MsgPackEncoder extends AbstractEncoder {

    String[] fields;

    /**
     * 如果fields为空, 则不输出excludeFields
     */
    Set<String> excludeFields;

    private MessagePack msgpack = new MessagePack();

    @Override
    public void encode(TEvent event, String target) throws EncodeException {
        event.set(target, encode(event));
    }

    @Override
    public void encode(TEvent event, OutputStream output) throws EncodeException, IOException {
        writeEvent(event, output);
    }

    public boolean containsField(String fieldName) {
        if (excludeFields != null && excludeFields.contains(fieldName)) {
            return false;
        }
        return true;
    }

    protected void writeEvent(TEvent event, OutputStream output) throws IOException {
        Packer              packer = msgpack.createPacker(output);
        Map<String, Object> data   = new HashMap<>();
        if (this.fields == null) {
            Map<String, Object> fields = event.getFields();
            for (Map.Entry<String, Object> e : fields.entrySet()) {
                String key = e.getKey();
                Object val = e.getValue();
                if (val != null && containsField(key)) {
                    data.put(key, val);
                }
            }
        } else {
            for (String field : this.fields) {
                String key = field;
                Object val = null;
                if (field.contains(":")) {
                    String[] fs = field.split(":");
                    key = fs[0];
                    val = event.get(fs[1]);
                } else {
                    val = event.get(field);
                }
                if (val != null) {
                    data.put(key, val);
                }
            }
        }
        if (!data.isEmpty()) {
            packer.write(data);
            packer.close();
        }
    }
}
