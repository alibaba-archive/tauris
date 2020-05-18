package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

import java.io.InputStream;

@Type("scanner")
public interface TScannerBuilder extends TPlugin {

    TScanner create(InputStream in, TEventFactory factory);

}
