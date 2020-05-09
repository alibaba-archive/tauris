package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.metrics.Gauge;
import com.aliyun.tauris.plugins.input.BaseStreamInput;
import net.jpountz.lz4.LZ4FrameInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by zhanglei on 2019/10/28.
 */
public abstract class AbstractHttpInput extends BaseStreamInput {

    public static final String META_HEADERS     = "headers";
    public static final String META_PARAMS      = "params";
    public static final String META_QUERYSTRING = "querystring";
    public static final String META_METHOD      = "method";
    public static final String META_PATH        = "path";

    protected final Pattern MAX_BODY_SIZE_PATTERN = Pattern.compile("(\\d+)(k|m)?");

    protected static Counter REQUEST_COUNTER       = Counter.build().name("input_http_request_total").labelNames("id").help("http input request count").create().register();
    protected static Counter REQUEST_ERROR_COUNTER = Counter.build().name("input_http_request_error_total").labelNames("id", "status").help("http input request error count").create().register();
    protected static Gauge   REQUEST_AVAILABLE_PERMITS;

    @Required
    protected int port;

    protected String host = "0.0.0.0";

    protected String path = "/";

    protected String clientMaxBodySize = "20m";

    protected TAuthenticatorPack authenticator = new DefaultAuthenticatorPack();

    protected Map<String, String> responseHeaders;

    protected int successCode = 204;

    protected long maxBodySize = 0;

    protected int maxConcurrency = 200;

    protected int maxWaitingMillis = 0;

    protected Semaphore lock;

    public void doInit() throws TPluginInitException {
        logger = TLogger.getLogger(this);

        if (maxConcurrency > 0) {
            lock = new Semaphore(maxConcurrency, true);
            REQUEST_AVAILABLE_PERMITS = Gauge.build().name("input_http_available_permits").labelNames("id").help("http input available permits count").create().register();
        }

        Matcher maxBodySizeMatcher = MAX_BODY_SIZE_PATTERN.matcher(clientMaxBodySize);
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
    }

    protected boolean acquireLock() {
        if (lock != null) {
            try {
                if (maxWaitingMillis > 0) {
                    return lock.tryAcquire(maxWaitingMillis, TimeUnit.MILLISECONDS);
                } else {
                    lock.acquire();
                }
            } catch (InterruptedException e) {
                REQUEST_ERROR_COUNTER.labels(id(), "503").inc();
                return false;
            } finally {
                REQUEST_AVAILABLE_PERMITS.labels(id()).set(lock.availablePermits());
            }
        }
        return true;
    }

    protected void releaseLock() {
        if (lock != null) {
            lock.release();
            REQUEST_AVAILABLE_PERMITS.labels(id()).set(lock.availablePermits());
        }
    }

    protected int handleGetRequest(String path,
                                   String queryString,
                                   Map<String, Object> params,
                                   String method,
                                   Map<String, Object> headers) {
        TEvent event = getEventFactory().create();
        event.addMeta(META_HEADERS, headers);
        event.addMeta(META_PATH, path);
        event.addMeta(META_METHOD, method.toLowerCase());
        if (!StringUtils.isEmpty(queryString)) {
            event.addMeta(META_QUERYSTRING, queryString);
        }
        if (!params.isEmpty()) {
            event.addMeta(META_PARAMS, params);
        }
        try {
            putEvent(event);
            return successCode;
        } catch (InterruptedException ex) {
            return 503; // SERVICE_UNAVAILABLE;
        }
    }

    protected int handlePostContent(String path,
                                    String queryString,
                                    Map<String, Object> params,
                                    String method,
                                    Map<String, Object> headers,
                                    String contentEncoding,
                                    int contentLength,
                                    InputStream input) throws IOException {
        CompressType compressType = contentEncoding == null ? CompressType.none : CompressType.valueOf(contentEncoding);

        if (contentLength == 0) {
            return successCode;
        }
        InputStream in = input;
        if (contentLength > 0) {
            in = new BoundedInputStream(in, contentLength);
        }
        switch (compressType) {
            case lz4:
                in = new LZ4FrameInputStream(in);
                break;
            case gzip:
                in = new GZIPInputStream(in);
                break;
            case deflate:
                in = new InflaterInputStream(in);
                break;
            case none:
                break;
            default:
                REQUEST_ERROR_COUNTER.labels(id(), "415").inc(); // UNSUPPORTED_MEDIA_TYPE
                return 415;
        }
        try (TScanner scanner = getScanner(in)) {
            scanner.scan((event) -> {
                event.addMeta(META_PATH, path);
                event.addMeta(META_METHOD, method.toLowerCase());
                if (!StringUtils.isEmpty(queryString)) {
                    event.addMeta(META_QUERYSTRING, queryString);
                }
                if (!params.isEmpty()) {
                    event.addMeta(META_PARAMS, params);
                }
                event.addMeta(META_HEADERS, headers);
                try {
                    putEvent(event);
                    return true;
                } catch (InterruptedException e) {
                    logger.ERROR(e);
                    return false;
                }
            });
        } catch (DecodeException e) {
            logger.WARN2("decode error", e, e.getSource());
        } catch (Exception e) {
            logger.EXCEPTION(e);
            return 500;
        }
        return successCode;
    }
}
