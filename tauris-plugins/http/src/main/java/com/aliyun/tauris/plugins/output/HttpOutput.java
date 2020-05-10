package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.plugins.codec.DefaultPrinter;
import com.aliyun.tauris.plugins.http.CompressType;
import com.aliyun.tauris.plugins.http.TSigner;
import com.aliyun.tauris.TLogger;
import com.aliyun.tauris.utils.EventFormatter;
import net.jpountz.lz4.LZ4FrameOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("http")
public class HttpOutput extends BaseBatchOutput {


    public enum Method {
        /**
         * http get
         */
        get,
        /**
         * http head
         */
        head,
        /**
         * http post
         */
        post,
        /**
         * http put
         */
        put;
    }

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_http_total").labelNames("id", "status").help("http output event count").create().register();
    private static Counter RETRY_COUNTER  = Counter.build().name("output_http_retry_total").labelNames("id").help("http output retry count").create().register();

    private TLogger logger;

    Map<String, String> headers = new HashMap<>();

    @Required
    EventFormatter url;

    TSigner signer;

    int socketTimeout = 10;

    int connectTimeout = 10;

    String contentType;

    int  retryTimes    = 6; //写入失败后的重试次数
    long retryInterval = 10; //写入失败后重试间隔时间, 单位秒

    Method method = Method.post;

    TPrinter printer = new DefaultPrinter();

    /**
     * 最大连接数
     */
    int maxConnection = 200;

    /**
     * 每个主机地址的并发数
     */
    int maxPerRoute = 20;

    /**
     * 压缩类型
     */
    CompressType compressType = CompressType.none;

    private SSLConnectionSocketFactory sslsf;

    private RequestConfig requestConfig;

    private PoolingHttpClientConnectionManager connectionManager;

    private IdleConnectionEvictor idleConnectionEvictor;

    public HttpOutput() {
        logger = TLogger.getLogger(this);
    }

    public void init() throws TPluginInitException {
        super.init();
        if (contentType != null) {
            headers.put("Content-Type", contentType);
        }
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnection);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);

        RequestConfig.Builder rcb = RequestConfig.custom();
        rcb.setConnectTimeout(connectTimeout * 1000);
        rcb.setSocketTimeout(socketTimeout * 1000);
        requestConfig = rcb.build();
        try {
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();

            sslsf = new SSLConnectionSocketFactory(sslcontext);
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new TPluginInitException(e.getMessage());
        }
    }

    @Override
    public void start() throws Exception {
        super.start();
        idleConnectionEvictor = new IdleConnectionEvictor(connectionManager, 10, TimeUnit.SECONDS);
        idleConnectionEvictor.start();
    }

    @Override
    protected BatchWriteTask newTask() throws Exception {
        if (method == Method.get || method == Method.head) {
            return new GetHeadTask();
        } else {
            return new PostPutTask();
        }
    }

    @Override
    public void stop() {
        super.stop();
        idleConnectionEvictor.shutdown();
        connectionManager.shutdown();
    }

    static class TransferResult {
        final int    status;
        final String payload;

        public TransferResult(int status, String payload) {
            this.status = status;
            this.payload = payload;
        }
    }

    abstract class HttpTask extends BatchWriteTask {
        protected CloseableHttpClient client;

        public HttpTask() {
            HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
            clientBuilder.setSSLSocketFactory(sslsf).build();
            this.client = clientBuilder.setSSLSocketFactory(sslsf).build();
        }
    }

    class PostPutTask extends HttpTask {
        HttpEntityEnclosingRequestBase request;
        TPrinter                       printer;
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        public PostPutTask() throws Exception {
            String url = HttpOutput.this.url.format();
            request = Method.post == method ? new HttpPost(url) : new HttpPut(url);
            OutputStream output = null;
            switch (compressType) {
                case lz4:
                    output = new LZ4FrameOutputStream(data);
                    break;
                case gzip:
                    output = new GZIPOutputStream(data);
                    break;
                case deflate:
                    output = new DeflaterOutputStream(data);
                    break;
                default:
                    output = data;
            }
            if (compressType != CompressType.none) {
                request.setHeader("Content-Encoding", compressType.toString());
            }
            this.printer = HttpOutput.this.printer.wrap(output).withCodec(getCodec());
        }

        @Override
        protected void accept(TEvent event) throws EncodeException, IOException {
            printer.write(event);
        }

        @Override
        protected void execute() {
            try {
                printer.flush();
                printer.close();
                Map<String, String> headers = new HashMap<>(HttpOutput.this.headers);
                byte[] content = data.toByteArray();
                request.setHeader("X-Event-Count", String.valueOf(elementCount()));
                if (signer != null) {
                    signer.sign(headers, request, content);
                }
                headers.forEach(request::setHeader);
                request.setConfig(requestConfig);
                request.setEntity(new ByteArrayEntity(content));

                int count = 0;
                while (count < retryTimes + 1) {
                    if (sendRequest(request)) {
                        return;
                    } else {
                        try {
                            Thread.sleep(retryInterval * 1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                        count++;
                        RETRY_COUNTER.labels(id).inc();
                    }
                }
                OUTPUT_COUNTER.labels(id, "597").inc(elementCount());
            } catch (IOException e) {
                logger.ERROR("http post failed", e);
            }
        }

        /**
         * @param request 请求
         * @return 任务完成与否，如果需要重试则返回false
         */
        protected boolean sendRequest(HttpEntityEnclosingRequestBase request) {
            long now = System.currentTimeMillis();
            try {
                HttpResponse response = client.execute(request);
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String payload = "";
                if (status != 204 && entity != null && entity.getContentLength() > 0) {
                    payload = EntityUtils.toString(response.getEntity());
                }
                if (status >= 300) {
                    OUTPUT_COUNTER.labels(id, status + "").inc(elementCount());
                    logger.ERROR("send request to %s failed, status is %s, message is %s", url.format(), status, payload.trim());
                    return false;
                } else {
                    OUTPUT_COUNTER.labels(id, "" + status).inc(elementCount());
                }
            } catch (SocketTimeoutException e) {
                OUTPUT_COUNTER.labels(id, "598").inc(elementCount());
                logger.ERROR("send request to %s timeout, %d events losing, use %d msec", e, url.format(), elementCount(), System.currentTimeMillis() - now);
            } catch (Exception e) {
                OUTPUT_COUNTER.labels(id, "599").inc(elementCount());
                logger.ERROR("send request to %s failed", e, url.format());
            }
            return true;
        }
    }

    class GetHeadTask extends HttpTask {

        private List<HttpRequestBase> requests;

        public GetHeadTask() {
            this.requests = new ArrayList<>();
        }

        @Override
        protected void accept(TEvent event) throws EncodeException, IOException {
            HttpRequestBase request = newRequest(event);

            Map<String, String> headers = new HashMap<>(HttpOutput.this.headers);

            if (signer != null) {
                signer.sign(headers, request, null);
            }
            headers.forEach(request::setHeader);
            request.setConfig(requestConfig);
        }

        private HttpRequestBase newRequest(TEvent event) {
            if (method == Method.get) {
                return new HttpGet(HttpOutput.this.url.format(event));
            } else {
                return new HttpHead(HttpOutput.this.url.format(event));
            }
        }

        @Override
        protected void execute() {
            try {
                printer.flush();
                printer.close();

                int count = 0;
                while (count < retryTimes + 1) {
                    if (sendRequests()) {
                        return;
                    } else {
                        try {
                            Thread.sleep(retryInterval * 1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                        count++;
                        RETRY_COUNTER.labels(id).inc();
                    }
                }
                OUTPUT_COUNTER.labels(id, "597").inc(elementCount());
            } catch (IOException e) {
                logger.ERROR("http post failed", e);
            }
        }

        private boolean sendRequests() {
            HttpClientBuilder   clientBuilder = HttpClients.custom();
            CloseableHttpClient client        = clientBuilder.setSSLSocketFactory(sslsf).build();
            List<HttpRequestBase> fails = new ArrayList<>();
            for (HttpRequestBase request : requests) {
                try {
                    HttpResponse response = client.execute(request);
                    int status = response.getStatusLine().getStatusCode();
                    HttpEntity entity = response.getEntity();
                    String payload = "";
                    if (entity != null && entity.getContentLength() > 0) {
                        payload = EntityUtils.toString(response.getEntity());
                    }
                    if (status >= 300) {
                        OUTPUT_COUNTER.labels(id, status + "").inc(elementCount());
                        logger.ERROR("send request to %s failed, status is %s, message is %s", request.getURI(), status, payload.trim().substring(0, 256));
                        fails.add(request);

                    } else {
                        OUTPUT_COUNTER.labels(id, "" + status).inc();
                    }
                } catch (IOException e) {
                    logger.ERROR("send request to %s failed, cause by %s", e, request.getURI(), e.getMessage());
                    OUTPUT_COUNTER.labels(id, "599").inc(elementCount());
                }
            }
            requests = fails;
            return requests.isEmpty();
        }
    }
}
