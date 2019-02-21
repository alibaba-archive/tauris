package com.aliyun.tauris.expression;

import com.aliyun.tauris.annotations.Name;

/**
 * Created by ZhangLei on 17/5/26.
 */
@Name("string")
public class IsString implements IsType {

    @Override
    public boolean check(Object value) {
        return value instanceof String;
    }

    @Override
    public String toString() {
        return "string";
    }
}
