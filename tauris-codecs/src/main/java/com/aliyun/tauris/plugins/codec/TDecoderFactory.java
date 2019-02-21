package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.AbstractPluginFactory;
import com.aliyun.tauris.TDecoder;
import com.aliyun.tauris.TEncoder;

/**
 * Created by ZhangLei on 17/5/23.
 */
public class TDecoderFactory extends AbstractPluginFactory {

    public TDecoderFactory() {
        super(TDecoder.class);
    }
}
