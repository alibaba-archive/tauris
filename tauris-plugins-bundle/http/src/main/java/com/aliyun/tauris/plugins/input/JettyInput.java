package com.aliyun.tauris.plugins.input;


import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Gauge;
import com.aliyun.tauris.plugins.http.AbstractHttpInput;

import com.aliyun.tauris.TLogger;
import io.undertow.util.StatusCodes;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by ZhangLei on 16/12/9.
 */
@Name(value = "http", minor = "jetty", preferred = true)
public class JettyInput extends AbstractHttpInput {

    private Gauge httpQueueSize;

    int maxThreads    = 200;
    int minThreads    = 8;

    String healthCheckPath;
    String healthCheckResponse = "success";

    File accesslog;

    private Server server;

    private ServerConnector connector;

    private QueuedThreadPool pool;

    public void doInit() throws TPluginInitException {
        super.doInit();
        logger = TLogger.getLogger(this);

        httpQueueSize = Gauge.build().name("input_http_queue_size").labelNames("id").help("http request queue size").create().register();
        pool = new QueuedThreadPool(maxThreads, minThreads);
        server = new Server(pool);
        connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setHost(host);
        server.addConnector(connector);
        if (accesslog != null) {
            NCSARequestLog requestLog = new ExtendedNCSARequestLog();
            requestLog.setFilename(accesslog.getAbsolutePath());
            requestLog.setAppend(true);
            requestLog.setExtended(true);
            requestLog.setLogLatency(true);
            requestLog.setRetainDays(7);
            requestLog.setLogDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            requestLog.setLogTimeZone(TimeZone.getDefault().getID());
            server.setRequestLog(requestLog);
        }

        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        server.addBean(new ErrorHandler() {
            @Override
            protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message) throws IOException {
                if (code >= 500) {
                    for (Throwable th = (Throwable) request.getAttribute("javax.servlet.error.exception"); th != null; th = th.getCause()) {
                        logger.EXCEPTION(th);
                    }
                }
            }
        });

        ServletHandler handler = new ServletHandler();
        if (healthCheckPath != null) {
            handler.addServletWithMapping(new ServletHolder(new HealthCheckServlet()), healthCheckPath);
        }
        handler.addServletWithMapping(new ServletHolder(new HttpInputServlet()), path);
        server.setHandler(handler);
    }

    @Override
    public void run() throws Exception {
        try {
            logger.INFO("http plugin started, {%s:%s}", host, port);
            server.setStopAtShutdown(true);
            server.start();
        } catch (Exception e) {
            logger.ERROR("http plugin start failed", e);
        }
    }

    @Override
    public void close() {
        try {
            super.close();
            pool.stop();
            logger.INFO("http plugin queue thread pool stopped");
            connector.close();
            logger.INFO("http plugin connector closed");
            server.stop();
            logger.INFO("http plugin server stopped");
        } catch (Exception e) {
            logger.ERROR("http plugin stop failed", e);
        }
    }

    private Map<String, Object> paramsOfRequest(HttpServletRequest req) {
        Map<String, Object> params = new HashMap<>();
        for (String key : req.getParameterMap().keySet()) {
            String[] vs = req.getParameterValues(key);
            if (vs.length == 1) {
                params.put(key, vs[0]);
            } else {
                params.put(key, vs);
            }
        }
        return params;
    }

    private Map<String, Object> headersOfRequest(HttpServletRequest req) {
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> hs      = req.getHeaderNames();
        while (hs.hasMoreElements()) {
            String hn = hs.nextElement();
            headers.put(hn.toLowerCase().replace('-', '_'), req.getHeader(hn));
        }
        return headers;
    }

    private class HealthCheckServlet extends HttpServlet {

        private volatile boolean workingOnline;

        public HealthCheckServlet() {
            this.workingOnline = true;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            if (workingOnline) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(healthCheckResponse);
                return;
            }
            try {
                String remoteAddr = req.getRemoteAddr();
                if (InetAddress.getByName(remoteAddr).isLoopbackAddress()) {
                    if (req.getMethod().equalsIgnoreCase("delete")) {
                        workingOnline = false;
                        resp.setStatus(HttpServletResponse.SC_OK);
                        return;
                    }
                    if (req.getMethod().equalsIgnoreCase("put")) {
                        workingOnline = true;
                        resp.setStatus(HttpServletResponse.SC_OK);
                        return;
                    }
                }
            } catch (UnknownHostException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                String remoteAddr = req.getRemoteAddr();
                if (InetAddress.getByName(remoteAddr).isLoopbackAddress()) {
                    workingOnline = true;
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            } catch (UnknownHostException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                String remoteAddr = req.getRemoteAddr();
                if (InetAddress.getByName(remoteAddr).isLoopbackAddress()) {
                    workingOnline = false;
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);

            } catch (UnknownHostException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        @Override
        protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private class HttpInputServlet extends HttpServlet {

        @Override
        public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            HttpServletRequest  req  = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;

            if (!acquireLock()) {
                REQUEST_ERROR_COUNTER.labels(id(), "503").inc();
                resp.setStatus(StatusCodes.SERVICE_UNAVAILABLE);
                return;
            }

            try {
                REQUEST_COUNTER.labels(id()).inc();

                int code = authenticator.check(req);
                if (code != 200) {
                    resp.setStatus(code);
                    REQUEST_ERROR_COUNTER.labels(id(), "" + code).inc();
                    return;
                }

                if (req.getContentLengthLong() > 0 && req.getContentLengthLong() > maxBodySize) {
                    REQUEST_ERROR_COUNTER.labels(id(), "" + HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE).inc();
                    resp.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
                    return;
                }

                Map<String, Object> headers = headersOfRequest(req);

                int status = successCode;
                try {
                    if (req.getMethod().equalsIgnoreCase("post") || req.getMethod().equalsIgnoreCase("put")) {
                        handlePostAnPut(req, resp, headers);
                    } else if (req.getMethod().equalsIgnoreCase("get") || req.getMethod().equalsIgnoreCase("head")) {
                        handleGetAndHead(req, resp, headers);
                    } else {
                        REQUEST_ERROR_COUNTER.labels(id(), "" + HttpServletResponse.SC_METHOD_NOT_ALLOWED).inc();
                        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }
                } catch (Exception e) {
                    status = 500;
                    logger.EXCEPTION(e);
                }
                if (responseHeaders != null) {
                    responseHeaders.forEach(resp::setHeader);
                }
                resp.setStatus(status);
                httpQueueSize.labels(id()).set(pool.getQueueSize());
            } finally {
                releaseLock();
            }
        }

        private void handleGetAndHead(HttpServletRequest request, HttpServletResponse response, Map<String, Object> headers) {
            int status = handleGetRequest(request.getRequestURI(), request.getQueryString(), paramsOfRequest(request), request.getMethod(), headers);
            response.setStatus(status);
        }

        private void handlePostAnPut(HttpServletRequest request, HttpServletResponse response, Map<String, Object> headers) throws IOException {
            String contentEncoding = request.getHeader("Content-Encoding");
            int    contentLength   = request.getContentLength();
            int status = handlePostContent(request.getRequestURI(),
                                           request.getQueryString(),
                                           paramsOfRequest(request),
                                           request.getMethod(), headers,
                                           contentEncoding, contentLength, request.getInputStream());
            response.setStatus(status);
        }
    }

    public static class ExtendedNCSARequestLog extends NCSARequestLog {

        public ExtendedNCSARequestLog() {
        }

        public ExtendedNCSARequestLog(String filename) {
            super(filename);
        }

        @Override
        protected void logExtended(StringBuilder b, Request request, Response response) throws IOException {
            super.logExtended(b, request, response);

            String contentLength = request.getHeader(HttpHeader.CONTENT_LENGTH.toString());
            b.append(' ');
            if (contentLength == null)
                b.append("0 ");
            else {
                b.append(contentLength);
                b.append(" ");
            }

            String auth = request.getHeader(HttpHeader.AUTHORIZATION.toString());
            if (auth == null || !auth.startsWith("Basic "))
                b.append("\"-\"");
            else {
                String h = new String(Base64.getDecoder().decode(auth.substring(6)), "UTF-8");
                int c = h.indexOf(':');
                b.append('"');
                b.append(h.substring(0, c));
                b.append('"');
            }
        }
    }
}
