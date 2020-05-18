package com.aliyun.tauris;

import com.aliyun.tauris.metrics.Gauge;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Properties;

/**
 * Class PooledEventFactory
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class PooledEventFactory implements TEventFactory {

    private static final String CONFIG_PREFIX = "tauris.event.pool.config.";

    private Gauge borrowedEvents;
    private Gauge returnedEvents;
    private Gauge activedEvents;
    private Gauge idledEvents;

    private EventPool pool;

    public PooledEventFactory() {
        PooledFactory factory = new PooledFactory();
        pool = new EventPool(factory, newPoolConfig());

        activedEvents = Gauge.build().name("tauris_event_pool_actived").help("the number of events currently borrowed from the pool").create().register();
        idledEvents = Gauge.build().name("tauris_event_pool_idled").help("the number of events currently idle in this pool").create().register();
        borrowedEvents = Gauge.build().name("tauris_event_pool_borrowed_count").help("The total number of objects successfully borrowed from this pool over the lifetime of the pool").create().register();
        returnedEvents = Gauge.build().name("tauris_event_pool_returned_count").help("The total number of objects returned to this pool over the lifetime of the pool").create().register();
    }

    @Override
    public String getName() {
        return "pooled";
    }

    @Override
    public TEvent create() {
        try {
            PooledEvent event = pool.borrowObject();
            event.active();
            recordMetrics();
            return event;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public TEvent create(String source) {
        try {
            PooledEvent event = pool.borrowObject();
            event.active();
            event.setSource(source);
            recordMetrics();
            return event;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void recordMetrics() {
        activedEvents.set(pool.getNumActive());
        idledEvents.set(pool.getNumIdle());
        borrowedEvents.set(pool.getBorrowedCount());
        returnedEvents.set(pool.getReturnedCount());
    }

    public void returnEvent(PooledEvent event) {
        pool.returnObject(event);
        recordMetrics();
    }

    private static GenericObjectPoolConfig newPoolConfig() {
        GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
        cfg.setMaxTotal(1000);
        cfg.setMaxIdle(1000);
        cfg.setMinIdle(0);
        Properties props= System.getProperties();
        for (Object o : props.keySet()) {
            String key = (String)o;
            if (!key.startsWith(CONFIG_PREFIX)) {
                continue;
            }
            String value = props.getProperty(key);
            String configName = key.substring(CONFIG_PREFIX.length());
            try {
                BeanUtils.setProperty(cfg, configName, value);
            } catch (Exception e) {
                System.err.println(String.format("invalid pool config item:%s, %s", key, e.getMessage()));
            }
        }
        return cfg;
    }

    private class PooledFactory extends BasePooledObjectFactory<PooledEvent> {

        @Override
        public PooledEvent create() throws Exception {
            return new PooledEvent(PooledEventFactory.this);
        }

        @Override
        public PooledObject<PooledEvent> wrap(PooledEvent event) {
            return new DefaultPooledObject<>(event);
        }

        @Override
        public void destroyObject(PooledObject<PooledEvent> p) throws Exception {
            p.getObject().destroy();
        }

        @Override
        public void activateObject(PooledObject<PooledEvent> p) throws Exception {
            p.getObject().active();
        }

        @Override
        public void passivateObject(PooledObject<PooledEvent> p) throws Exception {
            p.getObject().clear();
        }
    }

    private static class EventPool extends GenericObjectPool<PooledEvent> {
        public EventPool(PooledObjectFactory<PooledEvent> factory, GenericObjectPoolConfig config) {
            super(factory, config);
        }
    }
}
