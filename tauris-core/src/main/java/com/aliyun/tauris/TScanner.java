package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * Class TScanner
 *
 * @author yundun-waf-dev
 * @date 2018-07-23
 */
@Type("scanner")
public interface TScanner extends TPlugin {

    TScanner wrap(InputStream in);

    TScanner withCodec(TDecoder codec, TEventFactory factory);

    void scan(Function<TEvent, Boolean> consumer) throws IOException, DecodeException;

    void close() throws IOException;
}
