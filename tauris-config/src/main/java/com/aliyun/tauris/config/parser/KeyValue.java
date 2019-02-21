package com.aliyun.tauris.config.parser;


import com.aliyun.tauris.config.parser.SimpleValue.StringValue;

/**
 * Created by ZhangLei on 16/12/14.
 */
public class KeyValue {

    private StringValue key;
    private Object value;

    public KeyValue(StringValue key, Object value) {
        this.key = key;
        this.value = value;
    }

    public StringValue getKey() {
        return key;
    }

    public Object value() {
        return value;
    }
}
