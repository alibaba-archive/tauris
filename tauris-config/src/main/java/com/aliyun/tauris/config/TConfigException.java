package com.aliyun.tauris.config;

/**
 * Created by ZhangLei on 16/10/25.
 */
public class TConfigException extends RuntimeException {

    public TConfigException(String message) {
        super(message);
    }

    public TConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
