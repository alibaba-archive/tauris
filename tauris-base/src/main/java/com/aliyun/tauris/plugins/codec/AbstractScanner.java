package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.TLogger;

import java.io.EOFException;
import java.io.IOException;
import java.util.function.Function;

/**
 * Class AbstractScanner
 *
 * @author yundun-waf-dev
 * @date 2018-09-14
 */
public abstract class AbstractScanner implements TScanner {

    protected TLogger logger;
    protected TDecoder codec = new PlainDecoder();
    protected TEventFactory factory;

    public AbstractScanner() {
        this.logger = TLogger.getLogger(this);
    }

    public AbstractScanner(TDecoder codec) {
        this.logger = TLogger.getLogger(this);
        this.codec = codec;
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
        this.codec = codec;
        this.factory = factory;
        return this;
    }

    protected abstract boolean hasNext();

    protected abstract TEvent next() throws IOException, DecodeException;
}
