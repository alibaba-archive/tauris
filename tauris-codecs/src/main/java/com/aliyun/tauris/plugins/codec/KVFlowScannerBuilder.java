package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.TScannerBuilder;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.TLogger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Class KVFlowPrinter
 *
 * @author yundun-waf-dev
 * @date 2018-12-06
 */
@Name("kvflow")
public class KVFlowScannerBuilder implements TScannerBuilder {

    private TLogger logger;

    Integer maxBodySize = 16 * 1024 * 1024;

    public KVFlowScannerBuilder() {
        this.logger = TLogger.getLogger(this);
    }

    @Override
    public TScanner create(InputStream in, TEventFactory factory) {
        return new KVFlowScanner(in, factory);
    }

    public class KVFlowScanner extends AbstractKVFlow implements TScanner {

        private InputStream input;

        private TEventFactory factory;

        private byte[] valueBuffer;

        public KVFlowScanner() {
            this.init();
        }

        public KVFlowScanner(InputStream input, TEventFactory factory) {
            this.input = input;
            this.factory = factory;
            this.init();
        }

        private void init() {
            this.valueBuffer = new byte[512];
        }

        @Override
        public void scan(Function<TEvent, Boolean> consumer) throws IOException, DecodeException {
            DataInputStream input = new DataInputStream(this.input);

            byte[] headerBytes = new byte[HEADER_LEN];
            input.readFully(headerBytes);

            DataInputStream header = new DataInputStream(new ByteArrayInputStream(headerBytes));

            if (!verify(new byte[]{header.readByte(), header.readByte()}, header.readByte())) {
                throw new DecodeException("invalid data flow or wrong version");
            }

            int keysLen      = header.readInt();
            int keyCount     = header.readInt();
            int bodyLen      = header.readInt();
            int elementCount = header.readInt();

            if (keyCount > MAX_KEY_COUNT) {
                throw new DecodeException("too may keys:" + keyCount);
            }

            if (bodyLen > maxBodySize) {
                throw new DecodeException("body too large:" + bodyLen);
            }

            byte[] keysBuffer = new byte[keysLen];
            input.readFully(keysBuffer);
            String[] keys = readKeys(new DataInputStream(new ByteArrayInputStream(keysBuffer)), keyCount);

            for (int elementIndex = 0; elementIndex < elementCount; elementIndex++) {
                TEvent event      = factory.create();
                int    valueCount = input.readInt();
                if (valueCount > keys.length) {
                    throw new DecodeException("too may values:" + valueCount);
                }
                for (int valueIndex = 0; valueIndex < valueCount; valueIndex++) {
                    int keyIndex = 0xFF & input.readByte();
                    if (keyIndex >= keys.length) {
                        throw new DecodeException("key index " + keyIndex + " output range " + keys.length);
                    }
                    String key  = keys[keyIndex];
                    byte   type = input.readByte();
                    switch (type) {
                        case TYPE_STRING:
                            int len = input.readInt();
                            if (len > MAX_VALUE_LEN) {
                                throw new DecodeException("value too long:" + len);
                            }
                            valueBuffer = ensureCapacity(valueBuffer, len);
                            input.readFully(valueBuffer, 0, len);
                            event.setField(key, new String(valueBuffer, 0, len));
                            break;
                        case TYPE_NULL:
                            event.setField(key, null);
                            break;
                        case TYPE_INT:
                            event.setField(key, input.readInt());
                            break;
                        case TYPE_LONG:
                            event.setField(key, input.readLong());
                            break;
                        case TYPE_FLOAT:
                            event.setField(key, input.readFloat());
                            break;
                        case TYPE_DOUBLE:
                            event.setField(key, input.readDouble());
                            break;
                        case TYPE_BOOL:
                            event.setField(key, input.readByte() == 1);
                            break;
                        default:
                            throw new DecodeException("invalid type:" + type);
                    }
                }
                consumer.apply(event);
            }
        }

        private boolean verify(byte[] magic, byte version) {
            return !(magic[0] != MAGIC_NUMBER[0] || magic[1] != MAGIC_NUMBER[1] || VERSION != version);
        }

        private String[] readKeys(DataInputStream input, int keyCount) throws IOException {
            String[]   keys    = new String[keyCount];
            byte[]     keyBuf  = new byte[MAX_KEY_LEN];
            for (int i = 0; i < keyCount; i++) {
                int len = input.readInt();
                input.readFully(keyBuf, 0, len);
                keys[i] = new String(keyBuf, 0, len, charset);
            }
            return keys;
        }

        private byte[] ensureCapacity(byte[] buffer, int minCapacity) {
            // overflow-conscious code
            if (minCapacity - buffer.length > 0) {
                return grow(buffer, minCapacity);
            }
            return buffer;
        }

        /**
         * Increases the capacity to ensure that it can hold at least the
         * number of elements specified by the minimum capacity argument.
         *
         * @param minCapacity the desired minimum capacity
         */
        private byte[] grow(byte[] buffer, int minCapacity) {
            // overflow-conscious code
            int oldCapacity = buffer.length;
            int newCapacity = oldCapacity << 1;
            if (newCapacity - minCapacity < 0) {
                newCapacity = minCapacity;
            }
            if (newCapacity < 0) {
                // overflow
                if (minCapacity < 0) {
                    throw new OutOfMemoryError();
                }
                newCapacity = Integer.MAX_VALUE;
            }
            buffer = Arrays.copyOf(buffer, newCapacity);
            return buffer;
        }

        @Override
        public void close() throws IOException {
            input.close();
        }
    }
//
//    static int read(final InputStream input, final byte[] buffer, final int offset, final int length)
//            throws IOException {
//        if (length < 0) {
//            throw new IllegalArgumentException("Length must not be negative: " + length);
//        }
//        int remaining = length;
//        while (remaining > 0) {
//            final int location = length - remaining;
//            final int count = input.read(buffer, offset + location, remaining);
//            if (-1 == count) { // EOF
//                break;
//            }
//            remaining -= count;
//        }
//        return length - remaining;
//    }
}
