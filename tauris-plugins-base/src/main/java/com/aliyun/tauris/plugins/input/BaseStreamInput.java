package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.TScanner;
import com.aliyun.tauris.TScannerBuilder;
import com.aliyun.tauris.plugins.codec.LineScannerBuilder;

import java.io.InputStream;

/**
 * Created by ZhangLei on 16/12/9.
 */
public abstract class BaseStreamInput extends BaseTInput {

    protected TScannerBuilder scanner = new LineScannerBuilder();

    public TScanner getScanner(InputStream in) {
        return scanner.create(in, eventFactory);
    }
}
