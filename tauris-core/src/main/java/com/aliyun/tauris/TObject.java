package com.aliyun.tauris;

import io.tauris.expression.Context;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public interface TObject extends Context {

    /**
     * get value by name
     * @param name context variable name
     * @return context variable value
     */
    Object get(String name);

}
