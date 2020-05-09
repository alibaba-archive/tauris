package com.aliyun.tauris;

/**
 * Class PooledEvent
 *
 * @author yundun-waf-dev
 * @date 2019-04-07
 */
public class PooledEvent extends DefaultEvent {

    private PooledEventFactory factory;

    private volatile boolean actived = true;

    public PooledEvent(PooledEventFactory factory) {
        this.factory = factory;
    }

    public PooledEvent(String source, PooledEventFactory factory) {
        super(source);
        this.factory = factory;
    }

    @Override
    public void set(String name, Object value) {
        if (!this.actived) {
            throw new IllegalStateException("event deactivated");
        }
        super.set(name, value);
    }

    @Override
    public Object get(String name) {
        if (!this.actived) {
            throw new IllegalStateException("event deactivated");
        }
        return super.get(name);
    }

    @Override
    protected void active() {
        actived = true;
        super.active();
    }

    @Override
    public void destroy() {
        actived = false;
        factory.returnEvent(this);
    }
}
