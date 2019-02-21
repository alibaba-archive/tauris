package com.aliyun.tauris.expression;

import com.aliyun.tauris.annotations.Name;

import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 17/5/26.
 */
@Name("ip4")
public class IsIP4Type implements IsType {

    private static final Pattern IP4_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+$");

    @Override
    public boolean check(Object value) {
        if (value == null || !(value instanceof String)) {
            return false;
        }
        String host = (String)value;
        return (IP4_PATTERN.matcher(host).matches());
    }

    @Override
    public String toString() {
        return "ip4";
    }
}
