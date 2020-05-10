package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.metrics.Gauge;
import com.aliyun.tauris.TScanner;
import com.aliyun.tauris.TLogger;
import com.aliyun.tauris.plugins.codec.LineScanner;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("tcp")
public class TCPInput extends BaseTInput {

    private static Gauge POOLSIZE  = Gauge.build().name("input_tcp_poolsize").labelNames("id").help("current connection pool size").create().register();
    private static Gauge POOLCOUNT = Gauge.build().name("input_tcp_poolcount").labelNames("id").help("current connection pool count").create().register();
    private static Gauge SOCKCOUNT = Gauge.build().name("input_tcp_sockscount").labelNames("id").help("current connection sockets count").create().register();

    private TLogger logger;

    @Required
    int port = 1980;
    String host            = null;
    int    maxClientConn   = 0;
    int    soTimeout       = 0;
    int    connectPoolSize = 100;

    TScanner scanner = new LineScanner();

    private ThreadPoolExecutor executorService;

    private volatile ServerSocket serverSocket;

    private Set<Socket> sockets = Sets.newConcurrentHashSet();

    private volatile int connects;

    public void doInit() {
        this.logger = TLogger.getLogger(this);
        if (connectPoolSize == 0) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<>());
        } else {
            executorService = new ThreadPoolExecutor(connectPoolSize, connectPoolSize,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(connectPoolSize));
        }
    }

    public void run() throws Exception {
        logger.info(String.format("tcp input starting, listen at %s", port));
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
                executorService.execute(new TCPReceiver(socket));
            } catch (SocketTimeoutException e) {
                logger.error("tcp connection timeout");
            } catch (IOException e) {
                logger.error("tcp io exception", e);
            } catch (Exception e) {
                logger.error("tcp unknown exception", e);
            }
        }
        logger.warn("tcp input has been quited");
    }

    @Override
    public void close() {
        super.close();
        logger.info("tcp input closing");
        IOUtils.closeQuietly(serverSocket);
        serverSocket = null;
        for (Socket socket : sockets) {
            if (!socket.isClosed()) {
                IOUtils.closeQuietly(socket);
            }
        }
        logger.info("tcp input all socket has been closed");
        executorService.shutdownNow();
        logger.info("tcp input has been closed");
    }

    class TCPReceiver implements Runnable {

        private Socket socket;

        public TCPReceiver(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                TScanner scanner = TCPInput.this.scanner.wrap(socket.getInputStream()).withCodec(codec, getEventFactory());
                scanner.scan((event) -> {
                    try {
                        putEvent(event);
                    } catch (InterruptedException e) {
                        return false;
                    }
                    return true;
                });
            } catch (DecodeException e) {
                logger.WARN2("decode error", e, e.getSource());
            } catch (IOException e) {
            }
        }
    }
}
