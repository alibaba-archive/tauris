package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.formatter.SimpleFormatter;
import com.aliyun.tauris.metrics.Counter;
import io.nats.client.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import java.time.Duration;

/**
 * NATS 输出插件
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("nats")
public class NatsOutput extends BaseTOutput {

    private static Logger logger = LoggerFactory.getLogger("tauris.output.nats");

    private static Counter SENT        = Counter.build().name("output_nats_sent_total").labelNames("id", "subject").help("Sent nats messages count").create().register();
    private static Counter SENT_FAILED = Counter.build().name("output_nats_sent_failed_total").labelNames("id").help("Sent nats failed messages count").create().register();

    @Required
    String[] servers;

    @Required
    SimpleFormatter subject;

    String connectionName;

    Integer bufferSize;

    Long connectionTimeoutMillis;

    Long reconnectWaitMillis;

    boolean noEcho;

    boolean noRandomize;

    boolean noReconnect;

    String token;

    String username;

    String password;

    Integer maxReconnects;

    Integer maxPingsOut;

    Integer maxControlLine;

    private Options options;

    private Connection connection;

    public void init() {
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
        if (reconnectWaitMillis != null) {
            o.reconnectWait(Duration.ofMillis(reconnectWaitMillis));
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
            }
        });
        options = o.build();
    }

    public void start() throws Exception {
        connection = Nats.connect(options);
    }

    public void doWrite(TEvent event) {
        try {
            String subject = this.subject.format(event);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                codec.encode(event, output);
                output.flush();
                String value = output.toString();
                connection.publish(subject, value.getBytes());
                SENT.labels(id(), subject).inc();
            } finally {
                IOUtils.closeQuietly(output);
            }
        } catch (EncodeException e) {
            logger.warn("encode error", e);
        } catch (IOException e) {
            SENT_FAILED.inc();
            logger.warn("send nats message failed", e);
        }
    }


    @Override
    public void stop() {
        super.stop();
        try {
            connection.close();
        } catch (Exception e) {
            logger.error("close connection failed", e);
        }
    }
}
