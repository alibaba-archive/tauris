package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.metrics.Gauge;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("kafka")
public class KafkaInput extends BaseTInput {

    private static Counter INPUT_COUNTER          = Counter.build().name("input_kafka_total").labelNames("id", "topic", "partition").help("kafka consume count").create().register();
    private static Gauge   INPUT_PARTITION_OFFSET = Gauge.build().name("input_kafka_partition_offset").labelNames("id", "topic", "partition").help("kafka partition offset").create().register();

    private TLogger logger;

    @Required
    String brokers;

    @Required
    String[] topic;

    @Required
    String groupId;

    Map<String, String> properties = new HashMap<>();

    private KafkaConsumer<Object, TEvent> consumer;

    private volatile boolean running;

    public KafkaInput() {
    }

    @Override
    public void doInit() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TEventDeserializer.class.getName());
        props.put("tauris.decoder", codec);
        props.put("tauris.logger", logger);
        props.put("tauris.event.factory", eventFactory);
        if (!this.properties.isEmpty()) {
            props.putAll(properties);
        }
        try {
            consumer = new KafkaConsumer<Object, TEvent>(props);
        } catch (Exception e) {
            e.printStackTrace();
            logger.EXCEPTION(e);
            throw new TPluginInitException("build kafka producer failed", e);
        }

    }

    public void run() throws Exception {
        consumer.subscribe(Arrays.asList(topic));
        running = true;
        while (running) {
            try {
                ConsumerRecords<Object, TEvent> events = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<Object, TEvent> record : events) {
                    if (record != null) {

                        TEvent event = record.value();
                        event.addMeta("partition", record.partition());
                        event.addMeta("offset", record.offset());
                        event.addMeta("topic", record.topic());
                        event.setTimestamp(record.timestamp());
                        putEvent(event);
                        INPUT_COUNTER.labels(id(), record.topic(), record.partition() + "").inc();
                        INPUT_PARTITION_OFFSET.labels(id(), record.topic(), record.partition() + "").set(record.offset());
                        ;
                    }
                }
            } catch (Exception e) {
                logger.EXCEPTION(e);
            }
        }
    }

    @Override
    public void close() {
        running = false;
        consumer.close();
    }

    public static class TEventDeserializer implements Deserializer<TEvent> {

        private TDecoder      codec;
        private TLogger       logger;
        private TEventFactory factory;

        public TEventDeserializer() {
        }

        @Override
        public void configure(Map map, boolean b) {
            this.codec = (TDecoder) map.get("tauris.decoder");
            this.logger = (TLogger) map.get("tauris.logger");
            this.factory = (TEventFactory) map.get("tauris.event.factory");
        }

        @Override
        public TEvent deserialize(String topic, byte[] data) {
            try {
                return codec.decode(data, factory);
            } catch (DecodeException e) {
                logger.WARN2("decode error", e, e.getMessage());
                return null;
            }
        }

        @Override
        public void close() {

        }
    }

}
