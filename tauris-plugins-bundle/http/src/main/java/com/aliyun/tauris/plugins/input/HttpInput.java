package com.aliyun.tauris.plugins.input;


import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TScanner;
import com.aliyun.tauris.metric.Counter;
import com.aliyun.tauris.plugins.http.CompressType;
import com.aliyun.tauris.plugins.http.DefaultAuthenticatorPack;
import com.aliyun.tauris.plugins.http.TAuthenticatorPack;
import com.aliyun.tauris.utils.TLogger;
import net.jpountz.lz4.LZ4FrameInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by ZhangLei on 16/12/9.
 */
public class HttpInput extends BaseStreamInput {

    private static Counter REQUEST_COUNTER       = Counter.build().name("input_http_request_total").labelNames("id").help("http input request count").create().register();
    private static Counter REQUEST_ERROR_COUNTER = Counter.build().name("input_http_request_error_total").labelNames("id", "status").help("http input request error count").create().register();

    @Required
    int port;

    String host = "0.0.0.0";

    String path = "/";

    int threads = 10;

    String healthCheckPath;
    String healthCheckResponse = "success";

    String clientMaxBodySize = "10m";

    File accesslog;

    TAuthenticatorPack authenticator = new DefaultAuthenticatorPack();

    /**
     * key: content-type
     */
    Map<String, TDecoder> additionalCodecs;

    Map<String, String> responseHeaders;

    int successCode = HttpServletResponse.SC_NO_CONTENT;

    private Server server;

    private ServerConnector connector;

    private QueuedThreadPool pool;

    private long maxBodySize = 0;

    public void doInit() throws TPluginInitException {
        logger = TLogger.getLogger(this);

        Pattern maxBodySizePattern = Pattern.compile("(\\d+)(k|m)?");
        Matcher maxBodySizeMatcher = maxBodySizePattern.matcher(clientMaxBodySize);
        if (maxBodySizeMatcher.matches()) {
            maxBodySize = Integer.parseInt(maxBodySizeMatcher.group(1));
            if (maxBodySizeMatcher.groupCount() == 2) {
                String u = maxBodySizeMatcher.group(2);
                if (u.equalsIgnoreCase("k")) {
                    maxBodySize = maxBodySize * 1024;
                }
                if (u.equalsIgnoreCase("m")) {
                    maxBodySize = maxBodySize * 1024 * 1024;
                }
            }
        } else {
            throw new TPluginInitException("client_max_body_size is invalid:" + clientMaxBodySize);
        }

        pool = new QueuedThreadPool();
        pool.setMaxThreads(threads);
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

    private TDecoder getCodec(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return codec;
        }
        contentType = contentType.toLowerCase();
        if (additionalCodecs != null && additionalCodecs.containsKey(contentType)) {
            return additionalCodecs.get(contentType);
        }
        return codec;
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

            REQUEST_COUNTER.labels(id()).inc();

            int code = authenticator.check(req);
            if (code != 200) {
                resp.setStatus(code);
                REQUEST_ERROR_COUNTER.labels(id(), "" + code).inc();
                return;
            }

            if (req.getContentLengthLong() > 0 && req.getContentLengthLong() > maxBodySize ) {
                resp.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
                return;
            }

            Map<String, Object> headers = new HashMap<>();
            Enumeration<String> hs      = req.getHeaderNames();
            while (hs.hasMoreElements()) {
                String hn = hs.nextElement();
                headers.put(hn.toLowerCase().replace('-', '_'), req.getHeader(hn));
            }

            if (req.getMethod().equalsIgnoreCase("post") || req.getMethod().equalsIgnoreCase("put")) {
                TDecoder codec = getCodec(req);
                handlePostAnPut(req, resp, headers, codec);
            } else if (req.getMethod().equalsIgnoreCase("get") || req.getMethod().equalsIgnoreCase("head")) {
                handleGetAndHead(req, resp, headers);
            } else {
                REQUEST_ERROR_COUNTER.labels(id(), "" + HttpServletResponse.SC_METHOD_NOT_ALLOWED).inc();
                resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            if (responseHeaders != null) {
                responseHeaders.forEach(resp::setHeader);
            }
            resp.setStatus(successCode);
        }

        private void handleGetAndHead(HttpServletRequest req, HttpServletResponse response, Map<String, Object> headers) {
            TEvent e = new TEvent();
            e.addMeta("headers", headers);
            e.addMeta("path", req.getRequestURI());
            e.addMeta("method", req.getMethod());
            String queryString = req.getQueryString();
            if (queryString != null) {
                Map<String, Object> params = new HashMap<>();
                for (String key : req.getParameterMap().keySet()) {
                    String[] vs = req.getParameterValues(key);
                    if (vs.length == 1) {
                        params.put(key, vs[0]);
                    } else {
                        params.put(key, vs);
                    }
                }
                e.addMeta("params", params);
                e.addMeta("querystring", req.getQueryString());
            }
            try {
                putEvent(e);
            } catch (InterruptedException ex) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            }
        }

        private void handlePostAnPut(HttpServletRequest req, HttpServletResponse response, Map<String, Object> headers, TDecoder codec) throws IOException {
            String       contentEncoding = req.getHeader("Content-Encoding");
            CompressType compressType    = contentEncoding == null ? CompressType.none : CompressType.valueOf(contentEncoding);

            InputStream in = null;
            switch (compressType) {
                case lz4:
                    in = new LZ4FrameInputStream(req.getInputStream());
                    break;
                case gzip:
                    in = new GZIPInputStream(req.getInputStream());
                    break;
                case deflate:
                    in = new InflaterInputStream(req.getInputStream());
                    break;
                case none:
                    in = req.getInputStream();
                    break;
                default:
                    REQUEST_ERROR_COUNTER.labels(id(), "" + HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE).inc();
                    response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                    return;
            }
            TScanner scanner = HttpInput.this.scanner.wrap(in).withCodec(codec);
            String queryString = req.getQueryString();
            Map<String, Object> params = new HashMap<>();
            if (!StringUtils.isEmpty(queryString)) {
                for (String key : req.getParameterMap().keySet()) {
                    String[] vs = req.getParameterValues(key);
                    if (vs.length == 1) {
                        params.put(key, vs[0]);
                    } else {
                        params.put(key, vs);
                    }
                }
            }
            try {
                List<TEvent> events = new LinkedList<>();
                scanner.scan((event) -> {
                    event.addMeta("path", req.getRequestURI());
                    event.addMeta("method", req.getMethod());
                    if (!StringUtils.isEmpty(queryString)) {
                        event.addMeta("querystring", req.getQueryString());
                    }
                    if (!params.isEmpty()) {
                        event.addMeta("params", params);
                    }
                    event.addMeta("headers", headers);
                    events.add(event);
                    return true;
                });
                putEvents(events);
            } catch (DecodeException e) {
                logger.WARN2("decode error", e, e.getSource());
            } catch (InterruptedException e) {
                logger.ERROR(e);
            }
            IOUtils.closeQuietly(in);
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
