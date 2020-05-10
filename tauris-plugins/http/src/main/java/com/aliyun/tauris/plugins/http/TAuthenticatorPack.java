package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ZhangLei on 2017/11/15.
 */
@Type
public interface TAuthenticatorPack extends TPlugin {

    int check(HttpServletRequest request);
}
