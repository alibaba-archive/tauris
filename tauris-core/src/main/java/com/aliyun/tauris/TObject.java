package com.aliyun.tauris;

import com.aliyun.tauris.expression.Context;

/**
 * Created by ZhangLei on 17/5/26.
 */
public interface TObject extends Context {

    /**
     * get value by name
     * @param name context variable name
     * @return context variable value
     */
    Object get(String name);

}
