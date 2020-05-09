package com.aliyun.tauris;

import com.alibaba.texpr.Context;

/**
 * Created by ZhangLei on 17/5/26.
 */
public interface TObject extends Context {

    Object get(String name);

}
