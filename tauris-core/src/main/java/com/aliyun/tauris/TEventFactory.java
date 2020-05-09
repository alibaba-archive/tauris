package com.aliyun.tauris;

/**
 * Class TEventFactory
 *
 * @author yundun-waf-dev
 * @date 2019-04-06
 */
public interface TEventFactory {

    String getName();

    TEvent create();

    TEvent create(String source);

}
