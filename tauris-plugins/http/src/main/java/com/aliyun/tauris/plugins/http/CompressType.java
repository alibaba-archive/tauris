package com.aliyun.tauris.plugins.http;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public enum CompressType {
    none(""), lz4("lz4"), gzip("gzip"), deflate("deflate");

    private String strValue;

    CompressType(String strValue) {
        this.strValue = strValue;
    }

    public String toString() {
        return strValue;
    }
}
