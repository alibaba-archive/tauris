package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.config.TConfigException;

/**
 * Created by ZhangLei on 16/12/21.
 */
public class EnvironValue extends Value  {

    private String value;

    public EnvironValue(String name) {
        name = name.substring(1, name.length() - 1);
        this.value = System.getProperty(name);
        if (this.value == null) {
            this.value = System.getenv(name);
        }
        if (this.value == null) {
            throw new TConfigException("there no environ variable `" + name + "`");
        }
    }

    @Override
    void _assignTo(TProperty property) throws Exception {
        property.set(value);
    }

    String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
