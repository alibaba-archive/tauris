package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class EncodePrinterBuilder
 *
 * @author yundun-waf-dev
 * @date 2018-11-16
 */
public abstract class EncodePrinterBuilder implements TPrinterBuilder {

    protected TEncoder codec = new PlainEncoder();

    public void setCodec(TEncoder codec) {
        this.codec = codec;
    }
}
