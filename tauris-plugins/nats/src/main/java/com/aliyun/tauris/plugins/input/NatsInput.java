package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.metrics.Gauge;
import com.aliyun.tauris.TLogger;
import io.nats.client.*;

import java.nio.charset.Charset;
import java.time.Duration;

/**
 * NATS输入插件
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("nats")
public class NatsInput extends BaseTInput {

    private static Counter RECEIVED = Counter.build().name("input_nats_received_total").labelNames("id", "subject").help("Received nats messages").create().register();


    private static Counter DELIVERED_COUNT       = Counter.build().name("input_nats_delivered_count").labelNames("id").help("the total number of messages delivered to this consumer, for all time").create().register();
    private static Counter PENDING_BYTE_COUNT    = Counter.build().name("input_nats_pending_byte_count").labelNames("id").help("the cumulative size of the messages waiting to be delivered/popped").create().register();
    private static Gauge   PENDING_BYTE_LIMIT    = Gauge.build().name("input_nats_pending_byte_limit").labelNames("id").help("the pending byte limit").create().register();
    private static Counter PENDING_MESSAGE_COUNT = Counter.build().name("input_nats_pending_message_count").labelNames("id").help("the number of messages waiting to be delivered/popped").create().register();
    private static Gauge   PENDING_MESSAGE_LIMIT = Gauge.build().name("input_nats_pending_message_limit").labelNames("id").help("the pending message limit").create().register();
    private static Counter DROPPED_COUNT         = Counter.build().name("input_nats_dropped_count").labelNames("id").help("the number of messages dropped from this consumer").create().register();

    private TLogger logger;

    @Required
    String[] servers;

    @Required
    String[] subject;

    String connectionName;

    Integer bufferSize;

    Long connectionTimeoutMillis;

    boolean noEcho;

    boolean noRandomize;

    boolean noReconnect;

    String token;

    String username;

    String password;

    Integer maxReconnects;

    Integer maxPingsOut;

    Integer maxControlLine;

    Charset charset = Charset.defaultCharset();

    private Options options;

    private Connection connection;

    private Dispatcher dispatcher;

    private volatile boolean running;

    @Override
    public void doInit() {
        this.logger = TLogger.getLogger(this);
        Options.Builder o = new Options.Builder();
        for (String server : servers) {
            if (server.startsWith("nats://")) {
                o.server(server);
            } else {
                o.server("nats://" + server);
            }
        }
        if (bufferSize != null) {
            o.bufferSize(bufferSize);
        }
        o.connectionName(connectionName);
        if (connectionTimeoutMillis != null) {
            o.connectionTimeout(Duration.ofMillis(connectionTimeoutMillis));
        }
        if (noEcho) {
            o.noEcho();
        }
        if (token != null) {
            o.token(token);
        }
        if (username != null && password != null) {
            o.userInfo(username, password);
        }
        if (noRandomize) {
            o.noRandomize();
        }
        if (noReconnect) {
            o.noReconnect();
        }
        if (maxReconnects != null) {
            o.maxReconnects(maxReconnects);
        }
        if (maxPingsOut != null) {
            o.maxPingsOut(maxPingsOut);
        }
        if (maxControlLine != null) {
            o.maxControlLine(maxControlLine);
        }
        o.connectionListener((conn, type) -> logger.info("nats connection: {} {}", id(), type.toString()));
        o.errorListener(new ErrorListener() {
            @Override
            public void errorOccurred(Connection conn, String error) {
                logger.warn("nats error occurred: {} {}", id(), error);
            }

            @Override
            public void exceptionOccurred(Connection conn, Exception exp) {
                logger.warn("nats exception occurred: {} {}", id(), exp.getMessage());
            }

            @Override
            public void slowConsumerDetected(Connection conn, Consumer consumer) {
                if (consumer.isActive()) {
                    DELIVERED_COUNT.labels(id()).inc(consumer.getDeliveredCount());
                    PENDING_BYTE_COUNT.labels(id()).inc(consumer.getPendingByteCount());
                    PENDING_BYTE_LIMIT.labels(id()).inc(consumer.getPendingByteLimit());
                    PENDING_MESSAGE_COUNT.labels(id()).inc(consumer.getPendingMessageCount());
                    PENDING_MESSAGE_LIMIT.labels(id()).inc(consumer.getPendingMessageLimit());
                    DROPPED_COUNT.labels(id()).inc(consumer.getDroppedCount());
                }
            }
        });
        options = o.build();
    }

    public void run() throws Exception {
        connection = Nats.connect(options);
        dispatcher = connection.@author Ray Chaung<rockis@gmail.com>
            try {
                TEvent event = codec.decode(new String(message.getData(), charset));
                if (message.getReplyTo() != null) {
                    event.addMeta("__replyto__", message.getReplyTo());
                }
                event.addMeta("__subject__", message.getSubject());
                putEvent(event);
                RECEIVED.labels(id(), message.getSubject()).inc();
            } catch (DecodeException e) {
                logger.WARN2(e.getMessage(), e, e.getSource());
            } catch (Exception e) {
                logger.EXCEPTION(e);
            }
        });
        for (String subject : this.subject) {
            if (subject.contains(":")) {
                String[] kv = subject.split(":");
                dispatcher.subscribe(kv[0], kv[1]);
            } else {
                dispatcher.subscribe(subject);
            }
        }

        running = true;
        while (running) {
            Thread.sleep(100);
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            for (String subject : this.subject) {
                if (subject.contains(":")) {
                    String[] kv = subject.split(":");
                    dispatcher.unsubscribe(kv[0]);
                } else {
                    dispatcher.subscribe(subject);
                }
            }
        } catch (Exception e) {
            logger.ERROR("unsubcribe subjects failed", e);
        }
        running = false;
        try {
            connection.close();
        } catch (Exception e) {
            logger.ERROR("nats connection close failed", e);
        }
    }
}
