package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEncoder;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;

import com.aliyun.tauris.TLogger;
import com.aliyun.tauris.metrics.Gauge;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Kafaka输出插件
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("kafka")
public class KafkaOutput extends BaseTOutput {


    private static Counter OUTPUT_COUNTER          = Counter.build().name("output_kafka_total").labelNames("id", "topic").help("kafka sent count").create().register();
    private static Gauge   OUTPUT_PARTITION_OFFSET = Gauge.build().name("output_kafka_partition").labelNames("id", "topic", "offset").help("kafka partition offset").create().register();
    private static Counter ERROR_COUNTER           = Counter.build().name("output_kafka_error_total").labelNames("id", "topic").help("kafka sent error count").create().register();

    private TLogger logger;

    @Required
    String brokers;

    @Required
    String topic;

    boolean async = true;

    String clientId;

    boolean verbose;

    String keyField;
    String partitionField;

    Map<String, String> properties = new HashMap<>();

    private KafkaProducer<Object, TEvent> producer;

    private SendCallback callback;

    public void init() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        if (clientId == null) {
            try {
                clientId = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                throw new TPluginInitException("cannot read hostname");
            }
        }

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TEventSerializer.class.getName());
        props.put("tauris.encoder", codec);
        props.put("tauris.logger", logger);
        if (!this.properties.isEmpty()) {
            props.putAll(properties);
        }
        try {
            producer = new KafkaProducer<>(props);
        } catch (Exception e) {
            logger.EXCEPTION(e);
            throw new TPluginInitException("build kafka producer failed", e);
        }
        callback = new SendCallback();
    }

    @Override
    protected void doWrite(TEvent event) {
        Object  key       = null;
        Integer partition = null;
        if (keyField != null) {
            key = event.get(keyField);
        }
        if (partitionField != null) {
            partition = ((Number) event.get(partitionField)).intValue();
        }
        ProducerRecord<Object, TEvent> rec = new ProducerRecord<>(topic, partition, event.getTimestamp(), key, event);
        if (async) {
            producer.send(rec, callback);
        } else {
            Future<RecordMetadata> f = producer.send(new ProducerRecord<>(topic, 0, event.getTimestamp(), key, event));
            try {
                callback.onCompletion(f.get(), null);
            } catch (Exception e) {
                callback.onCompletion(null, e);
            }
        }
    }

    @Override
    public void stop() {
        producer.flush();
        producer.close();
    }

    class SendCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception != null) {
                ERROR_COUNTER.labels(id(), topic).inc();
                if (verbose) {
                    logger.ERROR("send message error, cause by %s", exception, exception.getMessage());
                }
            } else {
                OUTPUT_COUNTER.labels(id(), topic).inc();
                OUTPUT_PARTITION_OFFSET.labels(id(), topic, metadata.partition() + "").set(metadata.offset());
            }
        }
    }

    public static class TEventSerializer implements Serializer<TEvent> {

        private TEncoder codec;
        private TLogger logger;

        public TEventSerializer() {
        }

        @Override
        public void configure(Map map, boolean b) {
            this.codec = (TEncoder) map.get("tauris.encoder");
            this.logger = (TLogger) map.get("tauris.logger");
        }

        @Override
        public byte[] serialize(String topic, TEvent event) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                codec.encode(event, bos);
                return bos.toByteArray();
            } catch (IOException | EncodeException e) {
                logger.WARN2("encode error", e, e.getMessage());
                return new byte[0];
            }
        }

        @Override
        public void close() {

        }
    }
}
