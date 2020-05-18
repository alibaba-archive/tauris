package com.aliyun.tauris;

/**
 * Class TEventFactory
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public interface TEventFactory {

    String getName();

    TEvent create();

    TEvent create(String source);

}
