package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.TLogger;
import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;
import org.msgpack.unpacker.Unpacker;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

/**
 * Class MsgPackScanner
 *
 * @author yundun-waf-dev
 * @date 2018-09-14
 */
@NotThreadSafe
public class MsgPackScanner implements TScanner {

    private TLogger logger;

    private MessagePack msgpack = new MessagePack();

    private Unpacker unpacker;

    private Template<Map<String, String>> mapTemplate = Templates.tMap(Templates.TString, Templates.TString);

    private TEventFactory eventFactory;

    private TEvent event;

    public MsgPackScanner() {
        logger = TLogger.getLogger(this);
    }

    @Override
    public void scan(Function<TEvent, Boolean> consumer) throws IOException {
        try {
            while (hasNext()) {
                try {
                    TEvent event = next();
                    if (!consumer.apply(event)) {
                        break;
                    }
                } catch (DecodeException e) {
                    logger.WARN2("decode error", e, e.getSource());
                }
            }
        } catch (EOFException e) {
        }
    }

    @Override
    public TScanner withCodec(TDecoder codec, TEventFactory factory) {
        this.eventFactory = factory;
        return this;
    }

    protected boolean hasNext() {
        try {
            if (unpacker == null) {
                throw new IllegalStateException("invoke wrap(InputStream in) before");
            }
            this.event = null;
            TEvent event = eventFactory.create();
            Map<String, String> dataMap = unpacker.read(mapTemplate);
            for (Map.Entry<String, String> e : dataMap.entrySet()) {
                String field = e.getKey();
                String value = e.getValue();
                event.setField(field, value);
            }
            this.event = event;
        } catch (IOException e) {
        }
        return this.event != null;
    }

    protected TEvent next() throws IOException, DecodeException {
        return event;
    }

    @Override
    public TScanner wrap(InputStream in) {
        MsgPackScanner scanner = new MsgPackScanner();
        scanner.unpacker = msgpack.createUnpacker(in);;
        return scanner;
    }

    @Override
    public void close() throws IOException {
        this.unpacker.close();
    }
}
