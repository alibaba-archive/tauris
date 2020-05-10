package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Class KVFlowPrinter
 *
 * @author yundun-waf-dev
 * @date 2018-12-06
 */
@Name("kvflow")
public class KVFlowPrinter extends AbstractKVFlow implements TPrinter {

    String[] fields;

    /**
     * 最大body大小
     * 超过则强制刷新
     */
    int maxBodySize = 8 * 1024 * 1024;

    private ByteBuffer headerBuffer;
    private ByteBuffer keysBuffer;
    private ByteBuffer elementBuffer;
    private ByteBuffer bodyBuffer;

    private OutputStream output;

    private Map<String, Integer> keyIndexMap;
    private int                  keyCount;
    private int                  elementCount;

    public KVFlowPrinter() {

    }

    public void init() {
    }

    @Override
    public TPrinter wrap(OutputStream out) {
        KVFlowPrinter printer = new KVFlowPrinter();
        printer.fields = fields;
        printer.output = out;
        printer.headerBuffer = ByteBuffer.allocate(HEADER_LEN);
        printer.headerBuffer.put(MAGIC_NUMBER);
        printer.headerBuffer.put(VERSION);
        printer.keyCount = 0;
        printer.keyIndexMap = new HashMap<>();
        printer.keysBuffer = ByteBuffer.allocate(MAX_KEY_COUNT * MAX_KEY_LEN);
        printer.bodyBuffer = ByteBuffer.allocate(INITIAL_BODY_SIZE);
        printer.elementBuffer = ByteBuffer.allocate(maxElementSize);
        return printer;
    }

    @Override
    public TPrinter withCodec(TEncoder codec) {
        return this;
    }

    @Override
    public void write(TEvent event) throws IOException, EncodeException {
        elementBuffer.rewind();
        try {
            if (fields == null) {
                Map<String, Object> fs = event.getFields();
                elementBuffer.putInt(fs.size());
                for (Map.Entry<String, Object> entry : fs.entrySet()) {
                    writeKeyValue(elementBuffer, entry.getKey(), entry.getValue());
                }
            } else {
                elementBuffer.putInt(fields.length);
                for (String field : fields) {
                    writeKeyValue(elementBuffer, field, event.get(field));
                }
            }
            if (bodyBuffer.position() + elementBuffer.position() > maxBodySize) {
                flush();
            }
            ensureCapacity(bodyBuffer.position() + elementBuffer.position());
            bodyBuffer.put(elementBuffer.array(), 0, elementBuffer.position());
            elementCount++;
        } catch (BufferOverflowException e) {
            throw new EncodeException("element's size cannot exceed " + maxElementSize + " bytes");
        }
    }

    @Override
    public void flush() throws IOException {
        // write keys
        String[] keyNames = new String[keyCount];
        for (Map.Entry<String, Integer> entry : keyIndexMap.entrySet()) {
            keyNames[entry.getValue()] = entry.getKey();
        }
        for (String keyName : keyNames) {
            byte[] keyBytes = keyName.getBytes();
            if (keyBytes.length > MAX_KEY_LEN) {
                throw new IOException(String.format("The key %s too long, cannot exceed %d bytes", keyName, MAX_KEY_LEN));
            }
            keysBuffer.putInt(keyBytes.length);
            keysBuffer.put(keyBytes);
        }
        headerBuffer.putInt(keysBuffer.position());
        headerBuffer.putInt(keyCount);
        headerBuffer.putInt(bodyBuffer.position());
        headerBuffer.putInt(elementCount);

        output.write(headerBuffer.array());
        output.write(keysBuffer.array(), 0, this.keysBuffer.position());

        // write elements
        output.write(this.bodyBuffer.array(), 0, this.bodyBuffer.position());

        output.flush();
        // reset
        this.headerBuffer.rewind();
        this.headerBuffer.put(MAGIC_NUMBER).put(VERSION);
        this.keyCount = 0;
        this.keyIndexMap.clear();
        this.keysBuffer.rewind();
        this.elementCount = 0;
        this.bodyBuffer.rewind();
    }

    @Override
    public void close() throws IOException {
        flush();
        output.close();
    }

    private void writeKeyValue(ByteBuffer output, String key, Object value) throws IOException {
        int keyIndex  = putKey(key);
        byte valueType = typeOf(value);
        output.put((byte)keyIndex);
        switch (valueType) {
            case TYPE_NULL:
                output.put(valueType);
                break;
            case TYPE_STRING:
                byte[] bs = ((String) value).getBytes(charset);
                if (bs.length > MAX_VALUE_LEN) {
                    throw new IOException(String.format("The value of key %s too long, cannot exceed %d bytes", key, MAX_VALUE_LEN));
                }
                output.put(valueType);
                output.putInt(bs.length);
                output.put(bs);
                break;
            case TYPE_BOOL:
                output.put(valueType);
                output.put((byte) ((Boolean) value ? 1 : 0));
                break;
            case TYPE_INT:
                output.put(valueType);
                output.putInt((Integer) value);
                break;
            case TYPE_LONG:
                output.put(valueType);
                output.putLong((Long) value);
                break;
            case TYPE_FLOAT:
                output.put(valueType);
                output.putFloat((Float) value);
                break;
            case TYPE_DOUBLE:
                output.put(valueType);
                output.putDouble((Double) value);
                break;
            case TYPE_OTHER:
                output.put(TYPE_STRING);
                byte[] bytes = value.toString().getBytes(charset);
                output.putInt(bytes.length);
                output.put(bytes);
                break;
            default:
                break;
        }
    }

    private int putKey(String key) {
        if (keyIndexMap.containsKey(key)) {
            return keyIndexMap.get(key);
        }
        int index = keyCount;
        keyIndexMap.put(key, keyCount);
        keyCount++;
        if (keyCount > MAX_KEY_COUNT) {
            throw new IllegalStateException("too many keys");
        }
        return index;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > bodyBuffer.capacity()) {
            int oldCapacity = bodyBuffer.capacity();
            int newCapacity = oldCapacity << 1;
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
            newBuffer.put(bodyBuffer.array());
            bodyBuffer = newBuffer;
        }
    }

}
