package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type
public interface TAuthenticatorPack extends TPlugin {

    int check(HttpServletRequest request);
}
