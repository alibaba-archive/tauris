package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.*;
import com.aliyun.tauris.plugins.codec.LineScannerBuilder;

import java.io.InputStream;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class BaseStreamInput extends BaseTInput implements TInput {

    protected TScannerBuilder scanner = new LineScannerBuilder();

    public TScanner getScanner(InputStream in) {
        return scanner.create(in, eventFactory);
    }
}
