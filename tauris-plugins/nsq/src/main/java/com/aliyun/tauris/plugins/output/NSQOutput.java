package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;

import com.sproutsocial.nsq.Config;
import com.sproutsocial.nsq.NSQException;
import com.sproutsocial.nsq.Publisher;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("nsq")
public class NSQOutput extends BaseTOutput {

    private static Logger logger = LoggerFactory.getLogger("tauris.output.nsq");

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_nsq_total").help("nsq put count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_nsq_error_total").help("nsq put error count").create().register();

    private final static int BATCH_SIZE  = 16 * 104; //
    private final static int BATCH_DELAY = 300;  // unit millis

    @Required
    String nsqd;

    String failoverNsqd;


    @Required
    String topic;

    boolean batch = false;

    String authSecret;

    String clientId;
    String hostname;

    /**
     * Valid range: 1000 <= heartbeat_interval <= configured_max (-1 disables heartbeats)
     * Defaults to --client-timeout / 2
     */
    Integer heartbeatInterval;
    Boolean tlsV1;

    /**
     * enable snappy feature negotiation (client compression) (default true)
     * cannot enable both snappy and deflate.
     */
    Boolean snappy = false;

    /**
     * enable deflate feature negotiation (client compression) (default true)
     */
    Boolean deflate = false;

    /**
     * max deflate compression level a client can negotiate (> values == > nsqd CPU usage) (default 6)
     * cannot enable both snappy and deflate.
     */
    Integer deflateLevel = 6;

    /**
     * % of messages to publish (float b/w 0 -> 1), 0 disables sampling, (default 0)
     */
    Integer sampleRate = 0;

    int batchSize  = BATCH_SIZE;
    int batchDelay = BATCH_DELAY; // unit millis

    private Publisher publisher;

    public void init() throws TPluginInitException {
        publisher = new Publisher(nsqd, failoverNsqd);
        if (batch) {
            publisher.setBatchConfig(topic, batchSize, batchDelay);
        }
        if (authSecret != null) {
            publisher.getClient().setAuthSecret(authSecret);
        }
        Config config = publisher.getConfig();
        if (clientId != null) {
            config.setClientId(clientId);
        }
        if (hostname != null) {
            config.setHostname(hostname);
        } else {
            try {
                config.setHostname(InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException e) {
                throw new TPluginInitException("cannot read hostname");
            }
        }
        if (sampleRate != null) {
            config.setSampleRate(sampleRate);
        }
        if (deflate && snappy) {
            throw new TPluginInitException("cannot enable both snappy and deflate.");
        }
        if (tlsV1 != null) {
            config.setTlsV1(tlsV1);
        }
        config.setDeflate(deflate);
        config.setDeflateLevel(deflateLevel);
        config.setSnappy(snappy);
        if (heartbeatInterval != null) {
            config.setHeartbeatInterval(heartbeatInterval);
        }
    }

    @Override
    protected void doWrite(TEvent event) {
        super.doWrite(event);
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                codec.encode(event, output);
                output.flush();
                String value = output.toString();
                if (batch) {
                    publisher.publishBuffered(topic, value.getBytes());
                } else {
                    publisher.publish(topic, value.getBytes());
                }
            } finally {
                IOUtils.closeQuietly(output);
            }
            OUTPUT_COUNTER.inc();
        } catch (EncodeException | IOException e) {
            ERROR_COUNTER.inc();
            logger.error(String.format("%s.encode failed", codec.getClass().getName()));
        } catch (NSQException e) {
            ERROR_COUNTER.inc();
            logger.error("write to nsqd failed", e);
        }
    }

    @Override
    public void stop() {
        super.stop();
        publisher.stop();
        logger.info("nsq output closed", id());
    }
}
