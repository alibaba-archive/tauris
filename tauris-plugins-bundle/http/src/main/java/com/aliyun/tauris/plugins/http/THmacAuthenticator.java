package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.annotations.Name;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by ZhangLei on 2017/11/15.
 */
@Name("hmac")
public class THmacAuthenticator implements TAuthenticator {

    public static final String AUTH_METHOD = "THmac";

    Map<String, String> secrets;

    private String accessKey;

    private String accessSecret;


    public THmacAuthenticator() {
    }

    public THmacAuthenticator(String accessKey, String accessSecret) {
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
    }

    public void init() {
        if (secrets == null) {
            this.secrets = new HashMap<>();
            this.secrets.put(accessKey, accessSecret);
        }
    }

    @Override
    public boolean check(String credential, HttpServletRequest request) {
        String pair = new String(Base64.getDecoder().decode(credential));
        if (!pair.contains(":")) {
            return false;
        }
        String accessKey = pair.substring(0, pair.indexOf(':'));
        String sign      = pair.substring(pair.indexOf(':') + 1);
        if (!secrets.containsKey(accessKey)) {
            return false;
        }
        String accessSecret = secrets.get(accessKey);
        String method = request.getMethod();

        Map<String, String> headers = new HashMap<>();
        for (Enumeration<String> h = request.getHeaderNames(); h.hasMoreElements();) {
            String name = h.nextElement();
            String value = request.getHeader(name);
            headers.put(name, value);
        }
        String nsign = THmacSigner.makeSignature(accessSecret, method, headers, request.getPathInfo(), request.getQueryString());
        return sign.equals(nsign);
    }
}
