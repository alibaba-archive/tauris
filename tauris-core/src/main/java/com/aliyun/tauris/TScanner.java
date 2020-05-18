package com.aliyun.tauris;


import java.io.IOException;
import java.util.function.Function;

/**
 * Class TScanner
 *
 * @author yundun-waf-dev
 * @date 2018-07-23
 */
public interface TScanner extends AutoCloseable {

    void scan(Function<TEvent, Boolean> consumer) throws IOException, DecodeException;

    void close() throws IOException;
    
}
