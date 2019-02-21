package com.aliyun.tauris.plugins.scroll;

import com.aliyun.tauris.metric.Counter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 16/10/27.
 */
public class ScrollTransport implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(ScrollTransport.class);

    private static final Counter OUTPUT_COUNTER        = Counter.build().name("output_scroll_total").labelNames("id").help("scroll output count").create().register();
    private static final Counter OUTPUT_TARGET_COUNTER = Counter.build().name("output_scroll_target_total").labelNames("id", "target").help("scroll output count to target").create().register();
    private static final Counter OUTPUT_BYTES_COUNTER  = Counter.build().name("output_scroll_send_bytes").labelNames("id", "target").help("the scroll output sent bytes").create().register();

    private String id;

    private final String target;

    private final ScrollWriter writer;

    private int timeout = 3000;

    private long retryInterval = 300;

    private volatile boolean running = true;

    private LinkedBlockingQueue<ScrollMessage> queue;

    public ScrollTransport(String id,
                           String target,
                           LinkedBlockingQueue<ScrollMessage> queue,
                           String scrollVersion,
                           String scrollHostname,
                           String scrollAppName,
                           String scrollToken) {
        this.id = id;
        this.target = target;
        this.queue = queue;
        this.writer = new ScrollWriter(scrollVersion, scrollHostname, scrollAppName, scrollToken);
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public String getTarget() {
        return target;
    }

    private Socket newSocket() throws IOException {
        try {
            Socket socket = new Socket();
            InetSocketAddress address = stringToSocketAddr(target);
            socket.connect(address, timeout);
            LOG.info(String.format("connect to %s success", target));
            return socket;
        } catch (java.net.ConnectException e) {
            LOG.error(String.format("connect to %s failed", target));
            throw e;
        }
    }

    @Override
    public void run() {
        Socket socket = null;
        while (running) {
            ScrollMessage ctx = null;
            try {
                if (socket == null) {
                    socket = newSocket();
                }
                ctx = queue.poll(100, TimeUnit.MILLISECONDS);
                if (ctx == null) {
                    continue;
                }
                transport(socket, ctx);
            } catch (InterruptedException e){
                break;
            } catch (IOException e) {
                if (ctx != null) {
                    try {
                        queue.put(ctx);
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException ex){
                        break;
                    }
                }
                socket = null;
            }
        }
        IOUtils.closeQuietly(socket);
    }

    private void transport(Socket socket, ScrollMessage message) throws IOException {
        try {
            writer.write(socket.getOutputStream(), message.getBytes());
            OUTPUT_COUNTER.labels(id).inc(message.getCount());
            OUTPUT_TARGET_COUNTER.labels(id, target).inc(message.getCount());
            OUTPUT_BYTES_COUNTER.labels(id, target).inc(message.getBytes().length);
        } catch (IOException e) {
            IOUtils.closeQuietly(socket);
            LOG.warn(String.format("connection with %s has been closed, cause by %s", target, e.getMessage()));
            throw e;
        }
    }

    public static InetSocketAddress stringToSocketAddr(String str) {
        try {
            String[] ps = str.split(":");
            return new InetSocketAddress(ps[0], Integer.parseInt(ps[1]));
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid address:" + str);
        }
    }

    public void close() {
        running = false;
    }
}
