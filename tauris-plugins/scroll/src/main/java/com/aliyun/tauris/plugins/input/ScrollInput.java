package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.metrics.Gauge;
import com.aliyun.tauris.plugins.scroll.ScrollReceiver;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("scroll")
public class ScrollInput extends BaseStreamInput {

    private static Logger logger = LoggerFactory.getLogger(ScrollInput.class);

    private static Gauge POOLSIZE  = Gauge.build().name("input_scroll_poolsize").labelNames("id").help("current connection pool size").create().register();
    private static Gauge POOLCOUNT = Gauge.build().name("input_scroll_poolcount").labelNames("id").help("current connection pool count").create().register();
    private static Gauge SOCKCOUNT = Gauge.build().name("input_scroll_sockscount").labelNames("id").help("current connection sockets count").create().register();

    String host            = null;
    int    port            = 1980;
    int    maxClientConn   = 200;
    int    soTimeout       = 0;
    int    connectPoolSize = 100;

    private ThreadPoolExecutor executorService;

    private volatile ServerSocket serverSocket;

    private Set<Socket> sockets = Sets.newConcurrentHashSet();

    private volatile int connects;

    public void doInit() {
        if (connectPoolSize == 0) {
            executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        } else {
            executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(connectPoolSize);
        }
    }

    public void run() throws Exception {
        logger.info(String.format("scroll input starting, listen at %s", port));
        logger.info(String.format("version:%s", System.getProperty("tauris.version", "dev")));
        logger.info(String.format("max client connections:%s", maxClientConn));
        serverSocket = new ServerSocket();
        if (host != null) {
            serverSocket.bind(new InetSocketAddress(host, port));
        } else {
            serverSocket.bind(new InetSocketAddress(port));
        }
        serverSocket.setReceiveBufferSize(128 * 1024);
        if (soTimeout > 0) {
            serverSocket.setSoTimeout(this.soTimeout);
        }
        while (serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                String remoteHost = socket.getInetAddress().getHostAddress();

                if (maxClientConn > 0 && connects >= maxClientConn) {
                    logger.warn("too many connections, connection denied");
                    IOUtils.closeQuietly(socket);
                    continue;
                }
                connects++;

                socket.setSoTimeout(this.soTimeout);
                logger.info(String.format("client %s:%s came in", remoteHost, socket.getPort()));
                SOCKCOUNT.labels(id).inc();
                sockets.add(socket);
                executorService.execute(new ScrollReceiver(socket, pipe, scanner, codec, getEventFactory(), (h) -> {
                    connects--;
                    POOLSIZE.labels(id).set(executorService.getPoolSize());
                    POOLCOUNT.labels(id).set(executorService.getActiveCount());
                    if (sockets.remove(socket)) {
                        SOCKCOUNT.labels(id).dec();
                    }
                }));
            } catch (SocketTimeoutException e) {
                logger.error("Scroll collector connection timeout");
            } catch (IOException e) {
                logger.error("Scroll collector io exception", e);
            } catch (Exception e) {
                logger.error("Scroll unknown exception", e);
            }
        }
        logger.warn("scroll input has been quited");
    }

    @Override
    public void close() {
        super.close();
        logger.info("scroll input closing");
        IOUtils.closeQuietly(serverSocket);
        serverSocket = null;
        for (Socket socket: sockets) {
            if (!socket.isClosed()) {
                IOUtils.closeQuietly(socket);
            }
        }
        logger.info("scroll input all socket has been closed");
        executorService.shutdownNow();
        logger.info("scroll input has been closed");
    }
}
