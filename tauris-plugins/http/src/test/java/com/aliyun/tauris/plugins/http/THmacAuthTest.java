package com.aliyun.tauris.plugins.http;

import org.apache.http.client.methods.HttpPut;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class THmacAuthTest {

    @Test
    public void test() {

        String username = "chuanshi";
        String password = "hello";
        String content = "world";

        THmacSigner signer = new THmacSigner(username, password);
        signer.init();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Random", "1234");

        HttpPut request = new HttpPut("http://localhost/query?z=1&c=10");
        signer.sign(headers, request, content.getBytes());
        headers.forEach(request::setHeader);

        String a = headers.get(TSigner.AUTHORIZATION_HEADER);
        Assert.assertNotNull(a);
        Assert.assertTrue(a.startsWith(THmacSigner.AUTH_METHOD));

        THmacAuthenticator authenticator = new THmacAuthenticator(username, password);
        authenticator.init();

        HttpServletRequest req = new HttpServletRequestAdapter(request);
        Assert.assertTrue(authenticator.check(a.replace(THmacSigner.AUTH_METHOD + " ", ""), req));

    }
}
