package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.*;
import com.aliyun.tauris.formatter.EventFormatter;
import com.aliyun.tauris.metrics.Counter;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ZhangLei on 16/12/9.
 */
public abstract class BaseTInput extends AbstractPlugin implements TInput {

    private static final Counter INPUT_COUNTER = Counter.build().name("input_event_count").labelNames("id").help("input plugin receives event count").create().register();

    protected Charset charset = Charset.defaultCharset();

    protected TLogger logger;

    protected TEventFactory eventFactory;

    protected Map<String, Object> newFields;

    protected TPipe<TEvent> pipe;

    private Map<String, Object> _newFields = new ConcurrentHashMap<>();

    public void init() throws TPluginInitException {
        logger = TLogger.getLogger(this);

        if (newFields != null) {
            _newFields.clear();
            for (Map.Entry<String, Object> e : newFields.entrySet()) {
                String key = e.getKey();
                Object val = e.getValue();
                if (val instanceof String && ((String) val).contains("%{")) {
                    try {
                        EventFormatter formatter = EventFormatter.build((String) val);
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

    protected void doInit() throws TPluginInitException {
    }

    @Override
    public void init(TPipe<TEvent> pipe, TEventFactory eventFactory) {
        this.pipe = new TPipeDelegate(pipe);
        this.eventFactory = eventFactory;
    }

    @Override
    public void close() {
        this.release();
    }

    protected void putEvent(TEvent event) throws InterruptedException {
        pipe.put(event);
    }

    protected void putEvents(List<TEvent> events) throws InterruptedException {
        for (TEvent event: events) {
            pipe.put(event);
        }
    }

    protected TEventFactory getEventFactory() {
        return eventFactory;
    }

    class TPipeDelegate implements TPipe<TEvent> {

        private TPipe<TEvent> pipe;

        public TPipeDelegate(TPipe<TEvent> pipe) {
            this.pipe = pipe;
        }

        @Override
        public String getName() {
            return pipe.getName();
        }

        @Override
        public void put(TEvent event) throws InterruptedException {
            if (event == null) {
                return;
            }
            if (_newFields != null) {
                for (Map.Entry<String, Object> entry : _newFields.entrySet()) {
                    String key = entry.getKey();
                    Object val = entry.getValue();
                    if (val instanceof EventFormatter) {
                        val = ((EventFormatter) val).format(event);
                    }
                    if (!event.contains(key)) {
                        try {
                            event.set(key, val);
                        } catch (Exception ex) {
                            logger.EXCEPTION(ex);
                            return;
                        }
                    }
                }
            }
            pipe.put(event);
            INPUT_COUNTER.labels(id()).inc();
        }
    }
}
