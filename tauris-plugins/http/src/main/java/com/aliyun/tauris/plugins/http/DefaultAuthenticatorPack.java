package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.annotations.Name;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ZhangLei on 2017/11/20.
 */
@Name("default")
public class DefaultAuthenticatorPack implements TAuthenticatorPack {

    BasicAuthenticator basic;

    THmacAuthenticator hmac;

    @Override
    public int check(HttpServletRequest request) {
        if (basic == null && hmac == null) {
            return 200;
        }
        String a = request.getHeader(TSigner.AUTHORIZATION_HEADER);
        if (a == null) {
            return 401;
        }
        String[] ps = a.split(" ");
        if (ps.length != 2) {
            return 403;
        }
        String method = ps[0];
        String credential = ps[1];
        if (method.equalsIgnoreCase(BasicAuthenticator.AUTH_METHOD) && basic != null) {
            return basic.check(credential, request) ? 200 : 403;
        }
        if (method.equalsIgnoreCase(THmacAuthenticator.AUTH_METHOD) && hmac != null) {
            return hmac.check(credential, request) ? 200 : 403;
        }
        return 403;
    }
}
