package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.plugins.scroll.Scroll;
import com.aliyun.tauris.plugins.scroll.ScrollMessage;
import com.aliyun.tauris.plugins.scroll.ScrollTransport;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class ScrollOutput extends BaseTOutput {

    private static Logger logger = LoggerFactory.getLogger("tauris.output.scroll");

    private static final Counter DISCARD_COUNTER = Counter.build().name("output_scroll_discard").labelNames("id").help("scroll output discard event count").create().register();

    @Required
    String[] targets;

    /**
     * socket连接超时时间, 毫秒
     */
    int connectionTimeout = 3000;

    /**
     * 与target的连接数
     */
    int connectionCount = 8;

    /**
     * 每次最多发送的日志字节数, 默认1M
     */
    int maxBodySize = Scroll.MAX_BODY_LEN;

    /**
     * 每隔$linger毫秒发送一次, 即使缓存中的日志没有达到上限
     */
    long linger = 3000;

    /**
     * 发送失败重试间隔时间，单位毫秒
     */
    long retryInterval = 1000;

    /**
     * 在无法连接server且队列满的情况下，丢弃后续的事件
     */
    boolean noBlock = false;

    String version = "1.0";
    String appName = "tauris";
    String token   = "";

    private ExecutorService executorService;

    private List<ScrollTransport> transports = new ArrayList<>();

    private LinkedBlockingQueue<TEvent>        bufferQueue = new LinkedBlockingQueue<>(1000);
    private LinkedBlockingQueue<ScrollMessage> transQueue  = new LinkedBlockingQueue<>(1000);

    public void init() throws TPluginInitException {
        if (maxBodySize > Scroll.MAX_BODY_LEN) {
            throw new TPluginInitException("the max body length of scroll too large");
        }
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new TPluginInitException(e.getMessage());
        }
        executorService = Executors.newFixedThreadPool(connectionCount * targets.length + 1);
        executorService.submit(new FlushTask());
        for (String target : targets) {
            for (int i = 0; i < connectionCount; i++) {
                ScrollTransport transport = new ScrollTransport(id, target, transQueue, version, hostname, appName, token);
                transport.setRetryInterval(retryInterval);
                transport.setTimeout(connectionTimeout);
                executorService.submit(transport);
                transports.add(transport);
            }
        }
    }

    @Override
    public void doWrite(TEvent event) {
        try {
            bufferQueue.put(event);
        } catch (InterruptedException e) {
            logger.error("buffer queue has been interrupted");
        }
    }

    private void transport(ScrollMessage message) {
        if (message == null || message.isEmpty()) return;
        try {
            if (noBlock) {
                if (!transQueue.offer(message)) {
                    DISCARD_COUNTER.labels(id()).inc(message.getCount());
                }
            } else {
                transQueue.put(message);
            }
        } catch (InterruptedException e) {
            logger.error("transport queue has been interrupted, {} bytes ({} events) has been discards", message.getBytes().length, message.getCount());
        }
    }

    @Override
    public void stop() {
        super.stop();
        logger.info("scroll output {} closing", id);
        for (ScrollTransport transport: transports) {
            transport.close();
        }
        int times = 3;
        while (times >= 0) {
            if (!transQueue.isEmpty()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
            times--;
        }
        if (!transQueue.isEmpty() ) {
            logger.warn("transport queue was not empty, discards {} records", transQueue.size());
            transQueue.clear();
        }
        logger.info("scroll output {} clear queue", id);
        logger.info("scroll output {} shutdown scheduler", id);
        executorService.shutdownNow();
        logger.info("scroll output {} closed", id);
    }

    class FlushTask implements Runnable {

        public FlushTask() {
        }

        @Override
        public void run() {
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                int counter = 0;
                while (true) {
                    TEvent event = bufferQueue.poll(linger, TimeUnit.MILLISECONDS);
                    if (event == null) {
                        transport(new ScrollMessage(output.toByteArray(), counter));
                        output.reset();
                        counter = 0;
                    } else {
                        try {
                            getCodec().encode(event, output);
                            output.flush();
                            counter++;
                        } catch (EncodeException e) {
                            logger.warn("encode event error", e);
                        }
                        if (output.size() > Scroll.MAX_BODY_LEN) {
                            transport(new ScrollMessage(output.toByteArray(), counter));
                            output.reset();
                            counter = 0;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                logger.error("uncatched exception", e);
            }
            logger.warn("scroll output {} flush task quit", id);
        }
    }
}
