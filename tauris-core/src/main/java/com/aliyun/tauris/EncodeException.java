package com.aliyun.tauris;

/**
 * Class EncodeException
 *
 * @author yundun-waf-dev
 * @date 2019-01-23
 */
public class EncodeException extends Exception {

    public EncodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncodeException(String message) {
        super(message);
    }

    public EncodeException(Throwable cause) {
        super(cause);
    }
}
