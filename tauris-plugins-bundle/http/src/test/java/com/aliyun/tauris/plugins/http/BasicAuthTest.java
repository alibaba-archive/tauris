package com.aliyun.tauris.plugins.http;

import org.apache.http.client.methods.HttpPut;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangLei on 2017/11/20.
 */
public class BasicAuthTest {

    @Test
    public void test() throws Exception {
        String username = "chuanshi";
        String password = "hello";
        String content = "world";

        BasicSigner signer = new BasicSigner(username, password);
        signer.init();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Random", "1234");

        HttpPut request = new HttpPut("http://localhost/query");
        signer.sign(headers, request, content.getBytes());
        headers.forEach(request::setHeader);

        String a = headers.get(TSigner.AUTHORIZATION_HEADER);
        Assert.assertNotNull(a);
        Assert.assertTrue(a.startsWith(BasicSigner.AUTH_METHOD));

        BasicAuthenticator authenticator = new BasicAuthenticator(username, password);
        authenticator.init();

        HttpServletRequest req = new HttpServletRequestAdapter(request);
        Assert.assertTrue(authenticator.check(a.replace(BasicSigner.AUTH_METHOD + " ", ""), req));
    }

    @Test
    public void test2() {
        Assert.assertTrue(Htpasswd.md5.verify("$apr1$eARZpv/a$gsRlwmtC9TtB71h87reS9.", "i8SiJZrkwIuPq4lq"));
    }
}
