package com.aliyun.tauris.expression;

import com.aliyun.tauris.annotations.Name;

/**
 * Created by ZhangLei on 17/5/26.
 */
@Name("null")
public class IsNullType implements IsType {

    @Override
    public boolean check(Object value) {
        return value == null;
    }

    @Override
    public String toString() {
        return "null";
    }
}
