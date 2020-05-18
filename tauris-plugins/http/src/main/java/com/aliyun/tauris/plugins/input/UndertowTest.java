package com.aliyun.tauris.plugins.input;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

/**
 * Class UndertowTest
 *
 * @author yundun-waf-dev
 * @date 2019-10-26
 */
public class UndertowTest {

    public static void main(String[] argv) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        System.out.println(String.format("method:%s", exchange.getRequestMethod()));
                        System.out.println(String.format("path:%s", exchange.getRequestPath()));
                        System.out.println(String.format("url:%s", exchange.getRequestURL()));
                        System.out.println(String.format("querystring:%s", exchange.getQueryString()));
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Hello World");
                    }
                }).build();
        server.start();
    }
}
