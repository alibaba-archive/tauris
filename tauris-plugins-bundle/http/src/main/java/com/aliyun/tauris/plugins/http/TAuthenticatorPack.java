package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.TPlugin;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ZhangLei on 2017/11/15.
 */
public interface TAuthenticatorPack extends TPlugin {

    int check(HttpServletRequest request);
}
