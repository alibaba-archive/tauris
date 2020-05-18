package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

import java.io.OutputStream;

/**
 * Class TPrinterBuilder
 *
 * @author yundun-waf-dev
 * @date 2018-07-23
 */
@Type
public interface TPrinterBuilder extends TPlugin {

    TPrinter create(OutputStream out);

}
