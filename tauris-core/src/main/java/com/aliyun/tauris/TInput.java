package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

import java.util.List;

/**
 * Created by ZhangLei on 16/12/7.
 */
@Type("input")
public interface TInput extends TPlugin {

    void init(TPipe<TEvent> queue, TEventFactory factory);

    void run() throws Exception ;

    void close();

}
