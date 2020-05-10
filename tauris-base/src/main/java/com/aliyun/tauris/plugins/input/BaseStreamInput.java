package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.*;
import com.aliyun.tauris.plugins.codec.LineScanner;
import com.aliyun.tauris.TScanner;

/**
 * Created by ZhangLei on 16/12/9.
 */
public abstract class BaseStreamInput extends BaseTInput implements TInput {

    protected TScanner scanner = new LineScanner();


}
