package com.aliyun.tauris.config;

/**
 * Created by ZhangLei on 16/10/25.
 */
public class TConfigError extends Error {

    public TConfigError(String message) {
        super(message);
    }

    public TConfigError(String message, Throwable cause) {
        super(message, cause);
    }

}
