package com.aliyun.tauris.plugins.tcp;

/**
 * Created by zhanglei on 2018/9/10.
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
