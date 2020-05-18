package com.aliyun.tauris.config;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class TConfigError extends Error {

    public TConfigError(String message) {
        super(message);
    }

    public TConfigError(String message, Throwable cause) {
        super(message, cause);
    }

}
