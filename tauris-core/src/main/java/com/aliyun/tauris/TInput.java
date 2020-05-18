package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

import java.util.List;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type("input")
public interface TInput extends TPlugin {

    void init(TPipe<TEvent> queue, TEventFactory factory);

    void run() throws Exception ;

    void close();

}
