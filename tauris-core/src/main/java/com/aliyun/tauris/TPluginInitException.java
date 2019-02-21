package com.aliyun.tauris;

/**
 * Created by ZhangLei on 16/12/7.
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
