package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.utils.MapperUtil;

import java.io.*;
import java.util.Base64;
import java.util.Map;


/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("klv")
public class KLVEncoder extends AbstractEncoder {

    int headerLen = 16;

    String header;

    String[] fields;

    boolean base64 = true;

    private byte[] headerBytes = new byte[headerLen];

    public void init() {
        headerBytes = new byte[headerLen];
        if (header != null) {
            byte[] hb = header.getBytes();
            int len = hb.length > 16 ? 16 : hb.length;
            System.arraycopy(hb, 0, headerBytes, 0, len);
        }
    }

    @Override
    public void encode(TEvent event, String target) throws EncodeException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            encode(event, bos);
            if (base64) {
                String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());
                event.set(target, base64);
            } else {
                event.set(target, new String(bos.toByteArray(), charset));
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void encode(TEvent event, OutputStream output) throws EncodeException, IOException {
        output.write(headerBytes);
        byte[] lenBuf = new byte[4];
        if (fields != null) {
            for (String key : fields) {
                Object val = event.get(key);
                writeKeyValue(key, val, output, lenBuf);
            }
        } else {
            Map<String, Object> fields = event.getFields();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                writeKeyValue(entry.getKey(), entry.getValue(), output, lenBuf);
            }
        }
    }

    private void writeKeyValue(String key, Object val, OutputStream output, byte[] lenBuf) throws IOException {
        if (val == null) {
            return;
        }
        String value = val.toString();
        byte[] keyBytes = key.getBytes(charset);
        byte[] valBytes = value.getBytes(charset);
        MapperUtil.int2lbytes(keyBytes.length, lenBuf, 0);
        output.write(lenBuf);
        output.write(keyBytes);
        MapperUtil.int2lbytes(valBytes.length, lenBuf, 0);
        output.write(lenBuf);
        output.write(valBytes);
    }
}
