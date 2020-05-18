package com.aliyun.tauris.metrics;

import com.aliyun.tauris.TLogger;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;
import org.xnio.Options;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class HttpMetricServer extends MetricServer {

    private TLogger logger;

    /**
     * 绑定的IP地址, 如192.168.1.1, 默认是127.0.0.1
     */
    private String host = "127.0.0.1";

    private int port = 0;

    private String path = "/metrics";

    private Undertow server;

    private boolean noComment;

    public HttpMetricServer(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.logger = TLogger.getLogger(this);
        this.noComment = "false".equals(System.getProperty("tauris.metric.comment", "false"));
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 通过VM参数创建MetricServer
     *
     * @return
     */
    public static HttpMetricServer createMetricServer() {
        String pt   = System.getProperty("tauris.metric.port");
        String path = System.getProperty("tauris.metric.path", "/metrics");
        String host = System.getProperty("tauris.metric.host", "127.0.0.1");
        if (pt != null) {
            try {
                return new HttpMetricServer(host, Integer.parseInt(pt), path);
            } catch (NumberFormatException e) {
                System.err.println("invalid metric port " + pt);
            }
        }
        return null;
    }

    @Override
    public void init() {
        PathHandler handler = new PathHandler();
        handler.addPrefixPath(path, new HttpInputHandler());
        server = Undertow.builder()
                .addHttpListener(port, host)
                .setServerOption(Options.WORKER_TASK_MAX_THREADS, 2)
                .setHandler(handler).build();

    }

    @Override
    public void start() {
        this.init();
        try {
            server.start();
            logger.INFO("metric server started, {%s:%s}", host, port);
        } catch (Exception e) {
            System.err.println("metric server start failed, cause by " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void shutdown() {
        try {
            server.stop();
            logger.INFO("http metric server has been shutdown");
        } catch (Exception e) {
            logger.ERROR(e);
        }
    }

    private class HttpInputHandler implements HttpHandler {

        private int               startTime;
        private CollectorRegistry registry;

        public HttpInputHandler() {
            this.startTime = (int) (System.currentTimeMillis() / 1000);
            this.registry = CollectorRegistry.defaultRegistry;
        }

        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            HttpString method = exchange.getRequestMethod();
            if (!method.equals(Methods.GET) || !exchange.getRequestPath().equalsIgnoreCase(path)) {
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
            } else {
                String metric = build(noComment || exchange.getQueryString().contains("nocomment"));
                exchange.setStatusCode(200);
                exchange.getRequestHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=UTF-8");
                exchange.getRequestHeaders().add(Headers.CONTENT_LENGTH, metric.length());
                exchange.getRequestHeaders().add(HttpString.tryFromString("X-Start-Time"), startTime);
                exchange.getResponseSender().send(metric);
            }
        }

        private String build(boolean noComment) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("# VERSION " + System.getProperty("tauris.version", "dev")).append("\n");
            write(sb, noComment, this.registry.metricFamilySamples());
            return sb.toString();
        }

        private void write(StringBuilder writer, boolean noComment,
                           Enumeration<Collector.MetricFamilySamples> mfs) throws IOException {
            /* See http://prometheus.io/docs/instrumenting/exposition_formats/
             * for the output format specification. */
            for (Collector.MetricFamilySamples samples : Collections.list(mfs)) {
                if (!noComment) {
                    writer.append("# HELP " + samples.name + " " + escapeHelp(samples.help) + "\n");
                    writer.append("# TYPE " + samples.name + " " + typeString(samples.type) + "\n");
                }
                for (Collector.MetricFamilySamples.Sample sample : samples.samples) {
                    writer.append(sample.name);
                    if (sample.labelNames.size() > 0) {
                        writer.append("{");
                        for (int i = 0; i < sample.labelNames.size(); ++i) {
                            String labelName  = sample.labelNames.get(i);
                            String labelValue = sample.labelValues.get(i);
                            writer.append(String.format("%s=\"%s\",", labelName, escapeLabelValue(labelValue)));
                        }
                        writer.append("}");
                    }
                    writer.append(" " + Collector.doubleToGoString(sample.value) + "\n");
                }
            }
        }
    }

    static String escapeHelp(String s) {
        return s.replace("\\", "\\\\").replace("\n", "\\n");
    }

    static String escapeLabelValue(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    static String typeString(Collector.Type t) {
        switch (t) {
            case GAUGE:
                return "gauge";
            case COUNTER:
                return "counter";
            case SUMMARY:
                return "summary";
            case HISTOGRAM:
                return "histogram";
            default:
                return "untyped";
        }
    }
}

