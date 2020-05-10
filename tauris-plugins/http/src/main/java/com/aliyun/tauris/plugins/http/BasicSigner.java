package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Base64;
import java.util.Map;

/**
 * Created by ZhangLei on 2017/11/15.
 */
@Name("basic")
public class BasicSigner implements TSigner {

    public static final String AUTH_METHOD = "Basic";

    @Required
    String username;

    @Required
    String password;

    private String _credential;

    public BasicSigner() {
    }

    public BasicSigner(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void init() {
        _credential = AUTH_METHOD + " " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    @Override
    public void sign(Map<String, String> headers, HttpUriRequest request, byte[] content) {
        headers.put(TSigner.AUTHORIZATION_HEADER, _credential);
    }
}
