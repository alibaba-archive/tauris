package com.aliyun.tauris.expression;

import com.aliyun.tauris.annotations.Name;

/**
 * Created by ZhangLei on 17/5/26.
 */
@Name("true")
public class IsTrueType implements IsType {

    @Override
    public boolean check(Object value) {
        return "true".equals(value);
    }

    @Override
    public String toString() {
        return "true";
    }
}
