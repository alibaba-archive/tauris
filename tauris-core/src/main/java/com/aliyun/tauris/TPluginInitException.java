package com.aliyun.tauris;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class TPluginInitException extends Exception {

    public TPluginInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public TPluginInitException(String message) {
        super(message);
    }

    public TPluginInitException(Throwable cause) {
        super(cause);
    }
}
