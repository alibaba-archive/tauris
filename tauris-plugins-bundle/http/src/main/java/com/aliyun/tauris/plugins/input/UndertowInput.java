package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.plugins.http.AbstractHttpInput;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.util.*;
import org.xnio.Options;

import java.util.*;

/**
 * Class UndertowInput
 *
 * @author yundun-waf-dev
 * @date 2019-10-26
 */
@Name(value = "http", minor = "undertow")
public class UndertowInput extends AbstractHttpInput {

    int maxThreads = 200;

    private Undertow server;

    public void doInit() throws TPluginInitException {
        super.doInit();
        logger = TLogger.getLogger(this);
        PathHandler handler = new PathHandler();
        handler.addPrefixPath(path, new HttpInputHandler());
        server = Undertow.builder()
                .addHttpListener(port, host)
                .setServerOption(Options.WORKER_TASK_MAX_THREADS, maxThreads)
                .setHandler(handler).build();
    }

    @Override
    public void run() throws Exception {
        try {
            logger.INFO("http plugin started, {%s:%s}", host, port);
            server.start();
        } catch (Exception e) {
            logger.ERROR("http plugin start failed", e);
        }
    }

    @Override
    public void close() {
        try {
            super.close();
            server.stop();
            logger.INFO("http plugin server stopped");
        } catch (Exception e) {
            logger.ERROR("http plugin stop failed", e);
        }
    }

    private Map<String, Object> paramsOfRequest(HttpServerExchange exchange) {
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, Deque<String>> entry : exchange.getQueryParameters().entrySet()) {
            if (entry.getValue().size() == 1) {
                params.put(entry.getKey(), entry.getValue().pop());
            } else {
                params.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
            }
        }
        return params;
    }

    private Map<String, Object> headersOfRequest(HttpServerExchange exchange) {
        Map<String, Object> headers = new HashMap<>();
        for (HeaderValues h : exchange.getRequestHeaders()) {
            headers.put(h.getHeaderName().toString().toLowerCase().replace('-', '_'), h.element());
        }
        return headers;
    }

    private class HttpInputHandler implements HttpHandler {

        private HttpHandler getHead = new HttpGetHeadInputHandler();
        private HttpHandler putPost = new BlockingHandler(new HttpPostPutInputHandler());

        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            if (!acquireLock()) {
                exchange.setStatusCode(StatusCodes.SERVICE_UNAVAILABLE);
                return;
            }

            try {
                REQUEST_COUNTER.labels(id()).inc();
                HttpString method = exchange.getRequestMethod();
                if (method.equals(Methods.GET) || method.equals(Methods.HEAD)) {
                    exchange.dispatch(getHead);
                } else if (method.equals(Methods.POST) || method.equals(Methods.PUT)) {
                    exchange.dispatch(putPost);
                } else {
                    exchange.setStatusCode(StatusCodes.METHOD_NOT_ALLOWED);
                    REQUEST_ERROR_COUNTER.labels(id(), "" + StatusCodes.METHOD_NOT_ALLOWED).inc();
                }
            } finally {
                releaseLock();
            }
        }
    }

    private abstract class HttpBaseInputHandler implements HttpHandler {

        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            try {
                if (exchange.getRequestContentLength() > maxBodySize) {
                    exchange.setStatusCode(StatusCodes.REQUEST_ENTITY_TOO_LARGE);
                    REQUEST_ERROR_COUNTER.labels(id(), "" + StatusCodes.REQUEST_ENTITY_TOO_LARGE).inc();
                    return;
                }

                Map<String, Object> headers = headersOfRequest(exchange);
                handleRequest(exchange, headers);
                if (responseHeaders != null) {
                    for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
                        exchange.getResponseHeaders().add(HttpString.tryFromString(entry.getKey()), entry.getValue());
                    }
                }
                exchange.setStatusCode(successCode);
            } catch (Exception e) {
                if (exchange.isResponseChannelAvailable()) {
                    logger.EXCEPTION(e);
                    exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                    REQUEST_ERROR_COUNTER.labels(id(), "" + StatusCodes.INTERNAL_SERVER_ERROR).inc();
                }
            }
        }

        abstract void handleRequest(final HttpServerExchange exchange, Map<String, Object> headers) throws Exception;
    }

    private class HttpGetHeadInputHandler extends HttpBaseInputHandler {

        @Override
        void handleRequest(HttpServerExchange exchange, Map<String, Object> headers) throws Exception {
            int status = handleGetRequest(exchange.getRequestPath(), exchange.getQueryString(), paramsOfRequest(exchange), exchange.getRequestMethod().toString(), headers);
            exchange.setStatusCode(status);
        }
    }

    private class HttpPostPutInputHandler extends HttpBaseInputHandler {

        @Override
        void handleRequest(HttpServerExchange exchange, Map<String, Object> headers) throws Exception {
            HeaderValues contentEncodingValues = exchange.getRequestHeaders().get(Headers.CONTENT_ENCODING_STRING);
            String       contentEncoding       = contentEncodingValues == null ? null : contentEncodingValues.element();
            int          contentLength         = (int) exchange.getRequestContentLength();

            int status = handlePostContent(exchange.getRequestPath(),
                    exchange.getQueryString(),
                    paramsOfRequest(exchange),
                    exchange.getRequestMethod().toString(), headers,
                    contentEncoding, contentLength, exchange.getInputStream());
            exchange.setStatusCode(status);

        }
    }
}