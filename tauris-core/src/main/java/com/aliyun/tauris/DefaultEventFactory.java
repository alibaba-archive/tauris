package com.aliyun.tauris;

/**
 * Class DefaultEventFactory
 *
 * @author yundun-waf-dev
 * @date 2019-04-06
 */
public class DefaultEventFactory implements TEventFactory {

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public TEvent create() {
        return new DefaultEvent();
    }

    @Override
    public TEvent create(String source) {
        return new DefaultEvent(source);
    }
}
