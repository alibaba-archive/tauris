package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.utils.TLogger;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * Class KVFlowPrinter
 *
 * @author yundun-waf-dev
 * @date 2018-12-06
 */
@Name("kvflow")
public class KVFlowScanner extends AbstractKVFlow implements TScanner {

    private TLogger logger;

    private InputStream input;

    private byte[] valueBuffer;

    public KVFlowScanner() {
        this.logger = TLogger.getLogger(this);
    }

    @Override
    public TScanner wrap(InputStream in) {
        KVFlowScanner scanner = new KVFlowScanner();
        scanner.input = in;
        scanner.valueBuffer = new byte[MAX_VALUE_LEN];
        return scanner;
    }

    @Override
    public TScanner withCodec(TDecoder codec) {
        return this;
    }

    @Override
    public void scan(Function<TEvent, Boolean> consumer) throws IOException, DecodeException {
        byte[] headerBytes = new byte[HEADER_LEN];
        IOUtils.read(input, headerBytes);

        ByteBuffer header = ByteBuffer.wrap(headerBytes);

        if (!verify(new byte[]{header.get(), header.get()}, header.get())) {
            throw new DecodeException("invalid data flow or wrong version");
        }

        int keysLen      = header.getInt();
        int keyCount     = header.getInt();
        int bodyLen      = header.getInt();
        int elementCount = header.getInt();

        String[] keys = readKeys(input, keysLen, keyCount);

        byte[] bodyBytes = new byte[bodyLen];
        IOUtils.read(input, bodyBytes);
        ByteBuffer body = ByteBuffer.wrap(bodyBytes);

        for (int elementIndex = 0; elementIndex < elementCount; elementIndex++) {
            TEvent event = new TEvent();
            int valueCount = body.getInt();
            if (valueCount > keys.length) {
                throw new DecodeException("too may values");
            }
            for (int valueIndex = 0; valueIndex < valueCount; valueIndex++) {
                int keyIndex = 0xFF & body.get();
                if (keyIndex >= keys.length) {
                    throw new DecodeException("key index " + keyIndex + " output range " + keys.length);
                }
                String key = keys[keyIndex];
                byte type = body.get();
                switch (type) {
                    case TYPE_STRING:
                        int len = body.getInt();
                        body.get(valueBuffer, 0, len);
                        event.setField(key, new String(valueBuffer, 0, len));
                        break;
                    case TYPE_NULL:
                        event.setField(key, null);
                        break;
                    case TYPE_INT:
                        event.setField(key, body.getInt());
                        break;
                    case TYPE_LONG:
                        event.setField(key, body.getLong());
                        break;
                    case TYPE_FLOAT:
                        event.setField(key, body.getFloat());
                        break;
                    case TYPE_DOUBLE:
                        event.setField(key, body.getDouble());
                        break;
                    case TYPE_BOOL:
                        event.setField(key, body.get() == 1);
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

    private String[] readKeys(InputStream input, int keysLen, int keyCount) throws IOException {
        byte[] keysBytes = new byte[keysLen];
        IOUtils.read(input, keysBytes);

        ByteBuffer keysBuf = ByteBuffer.wrap(keysBytes);
        String[]   keys    = new String[keyCount];
        byte[]     keyBuf  = new byte[MAX_KEY_LEN];
        for (int i = 0; i < keyCount; i++) {
            int len = keysBuf.getInt();
            keysBuf.get(keyBuf, 0, len);
            keys[i] = new String(keyBuf, 0, len, charset);
        }
        return keys;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
