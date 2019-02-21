package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.AbstractPluginFactory;
import com.aliyun.tauris.TEncoder;
import com.aliyun.tauris.TFilter;

/**
 * Created by ZhangLei on 17/5/23.
 */
public class TEncoderFactory extends AbstractPluginFactory {

    public TEncoderFactory() {
        super(TEncoder.class);
    }
}
