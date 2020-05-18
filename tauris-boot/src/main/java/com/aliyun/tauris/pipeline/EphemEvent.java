package com.aliyun.tauris.pipeline;

import com.aliyun.tauris.TEvent;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class EphemEvent
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class EphemEvent implements TEvent {

    private TEvent realEvent;

    private volatile AtomicInteger lifePoint;

    public EphemEvent(TEvent realEvent, int lifePoint) {
        this.realEvent = realEvent;
        this.lifePoint = new AtomicInteger(lifePoint);
    }

    @Override
    public void addMeta(String key, Object value) {
        throw new UnsupportedOperationException("event has been frozen");
    }

    @Override
    public Map<String, Object> getMeta() {
        return realEvent.getMeta();
    }

    @Override
    public Object getMeta(String name) {
        return realEvent.getMeta(name);
    }

    @Override
    public boolean contains(String name) {
        return realEvent.contains(name);
    }

    @Override
    public void set(String name, Object value) {
        throw new UnsupportedOperationException("event has been frozen");
    }

    @Override
    public Object get(String name) {
        return realEvent.get(name);
    }

    @Override
    public Object remove(String name) {
        throw new UnsupportedOperationException("event has been frozen");
    }

    @Override
    public void setField(String name, Object value) {
        realEvent.setField(name, value);
    }

    @Override
    public Object removeField(String name) {
        throw new UnsupportedOperationException("event has been frozen");
    }

    @Override
    public void setFields(Map<String, Object> fields) {
        realEvent.setFields(fields);
    }

    @Override
    public Map<String, Object> getFields() {
        return realEvent.getFields();
    }

    @Override
    public long getTimestamp() {
        return realEvent.getTimestamp();
    }

    @Override
    public void setTimestamp(long timestamp) {
        throw new UnsupportedOperationException("event has been frozen");
    }

    @Override
    public void setSource(String source) {
        realEvent.setSource(source);
    }

    @Override
    public String getSource() {
        return realEvent.getSource();
    }

    @Override
    public void destroy() {
        int point = lifePoint.decrementAndGet();
        if (point == 0) {
            realEvent.destroy();
            realEvent = null;
        }
    }
}
