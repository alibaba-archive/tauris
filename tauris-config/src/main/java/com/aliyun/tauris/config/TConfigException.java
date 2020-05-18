package com.aliyun.tauris.config;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class TConfigException extends RuntimeException {

    public TConfigException(String message) {
        super(message);
    }

    public TConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
