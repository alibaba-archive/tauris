package com.aliyun.tauris.metric.server;

import com.aliyun.tauris.metric.CollectorRegistry;
import com.aliyun.tauris.metric.MetricServer;
import com.aliyun.tauris.utils.TLogger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class HttpMetricServer extends MetricServer {

    private TLogger logger;

    /**
     * 绑定的IP地址, 如192.168.1.1, 默认是127.0.0.1
     */
    private String host = "127.0.0.1";

    private int port = 0;

    private String path = "/metrics";

    private Server server;

    public HttpMetricServer(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.logger = TLogger.getLogger(this);
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
        QueuedThreadPool pool = new QueuedThreadPool();
        pool.setMaxThreads(10);
        server = new Server(pool);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setHost(host);
        connector.setReuseAddress(true);
        server.addConnector(connector);

        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        server.addBean(new ErrorHandler() {
            @Override
            protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message) throws IOException {
                if (code >= 500) {
                    for(Throwable th = (Throwable)request.getAttribute("javax.servlet.error.exception"); th != null; th = th.getCause()) {
                        logger.EXCEPTION(th);
                    }
                }
            }
        });

        ServletHandler handler = new ServletHandler();
        HttpMetricsServlet servlet = new HttpMetricsServlet(CollectorRegistry.defaultRegistry);
        servlet.doInit();
        if (!path.equals("/")) {
            handler.addServletWithMapping(new ServletHolder(new DefaultServlet()), "/");
        }
        handler.addServletWithMapping(new ServletHolder(servlet), path);
        server.setStopAtShutdown(true);
        server.setStopTimeout(1000);
        server.setHandler(handler);
    }

    @Override
    public void start()  {
        this.init();
        try {
            logger.INFO("metric server started, {%s:%s}", host, port);
            server.start();
        } catch (Exception e) {
            System.err.println("metric server start failed, cause by " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void shutdown() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.ERROR(e);
        }
    }
}

