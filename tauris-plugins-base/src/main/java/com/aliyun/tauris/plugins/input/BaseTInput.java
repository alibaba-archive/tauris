package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.*;
import com.aliyun.tauris.plugins.codec.PlainDecoder;
import com.aliyun.tauris.formatter.SimpleFormatter;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metric.Counter;
import com.aliyun.tauris.utils.TLogger;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 16/12/9.
 */
public abstract class BaseTInput extends AbstractPlugin implements TInput {

    private static final Counter INPUT_COUNTER = Counter.build().name("input_event_count").labelNames("id").help("input plugin receives event count").create().register();

    protected      Charset charset = Charset.defaultCharset();

    protected TLogger logger;

    TDecoder codec = new PlainDecoder();

    Map<String, Object> newFields;

    protected TQueue<List<TEvent>> queue;

    private Map<String, Object> _newFields = new ConcurrentHashMap<>();

    public void init() throws TPluginInitException {
        logger = TLogger.getLogger(this);

        if (newFields != null) {
            _newFields.clear();
            for (Map.Entry<String, Object> e : newFields.entrySet()) {
                String key = e.getKey();
                Object val = e.getValue();
                if (val instanceof String && ((String)val).contains("%{")) {
                    try {
                        SimpleFormatter formatter = SimpleFormatter.build((String)val);
                        _newFields.put(key, formatter);
                    } catch (Exception ex) {
                        throw new TPluginInitException("invalid formatter expression for input plugin " + id() + ":'" + val + "'");
                    }
                } else {
                    _newFields.put(key, val);
                }
            }
        }
        doInit();
    }

    protected void doInit() throws TPluginInitException {}

    public void setQueue(TQueue<List<TEvent>> queue) {
        this.queue = queue;
    }

    @Override
    public void init(TQueue<List<TEvent>> queue) {
        this.queue = new TQueueDelegate(queue);
    }

    @Override
    public void close() {
        this.release();
    }

    protected void putEvent(TEvent event) throws InterruptedException {
        queue.put(Collections.singletonList(event), 1);
    }

    protected void putEvents(List<TEvent> events) throws InterruptedException {
        queue.put(events, events.size());
    }

    class TQueueDelegate implements TQueue<List<TEvent>> {

        private TQueue<List<TEvent>> queue;

        public TQueueDelegate(TQueue<List<TEvent>> queue) {
            this.queue = queue;
        }

        @Override
        public String getName() {
            return queue.getName();
        }

        @Override
        public void put(List<TEvent> events, int elementCount) throws InterruptedException {
            if (events == null || events.isEmpty()) {
                return;
            }
            if (_newFields != null) {
                for (Map.Entry<String, Object> entry : _newFields.entrySet()) {
                    for (TEvent event: events) {
                        String key = entry.getKey();
                        Object val = entry.getValue();
                        if (val instanceof SimpleFormatter) {
                            val = ((SimpleFormatter)val).format(event);
                        }
                        try {
                            event.set(key, val);
                        } catch (NullPointerException ex) {
                            logger.EXCEPTION(ex);
                        }
                    }
                }
            }
            queue.put(events, events.size());
            INPUT_COUNTER.labels(id()).inc(events.size());
        }

        @Override
        public boolean offer(List<TEvent> item, long millis, int elementCount) throws InterruptedException {
            return queue.offer(item, millis, elementCount);
        }

        @Override
        public List<TEvent> take() throws InterruptedException {
            return queue.take();
        }

        @Override
        public List<TEvent> poll(long timeout, TimeUnit unit) throws InterruptedException {
            return queue.poll(timeout, unit);
        }

        @Override
        public boolean isEmpty() {
            return queue.isEmpty();
        }

        @Override
        public long getElementCount() {
            return queue.getElementCount();
        }

        @Override
        public long size() {
            return queue.size();
        }

        @Override
        public void clear() {
            queue.clear();
        }
    }
}
