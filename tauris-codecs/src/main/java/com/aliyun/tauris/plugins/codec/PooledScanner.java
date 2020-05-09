package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.TScannerBuilder;

/**
 * Class PooledScanner
 *
 * @author yundun-waf-dev
 * @date 2020-04-19
 */
public class PooledScanner {

    private TScannerBuilder factory;

    private volatile boolean actived = true;

    public PooledScanner(TScannerBuilder factory) {
        this.factory = factory;
    }


}

