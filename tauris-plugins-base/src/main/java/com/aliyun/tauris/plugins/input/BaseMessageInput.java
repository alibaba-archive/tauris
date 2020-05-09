package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.*;
import com.aliyun.tauris.plugins.codec.PlainDecoder;

/**
 * Created by ZhangLei on 16/12/9.
 */
public abstract class BaseMessageInput extends BaseTInput {

    protected TDecoder codec = new PlainDecoder();

    protected TDecoder getCodec() {
        return codec;
    }

}
