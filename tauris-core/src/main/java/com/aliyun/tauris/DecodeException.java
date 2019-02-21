package com.aliyun.tauris;

/**
 * Created by ZhangLei on 16/12/7.
 */
public class DecodeException extends Exception {

    private String source;

    public DecodeException(String message) {
        super(message);
    }

    public DecodeException(String message, Throwable cause, String source) {
        super(message, cause);
        this.source = source;
    }

    public DecodeException(String message, String source) {
        super(message);
        this.source = source;
    }

    public DecodeException(Throwable cause, String source) {
        super(cause);
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
