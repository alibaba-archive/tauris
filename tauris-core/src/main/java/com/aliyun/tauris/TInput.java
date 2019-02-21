package com.aliyun.tauris;

import java.util.List;

/**
 * Created by ZhangLei on 16/12/7.
 */
public interface TInput extends TPlugin {

    void init(TQueue<List<TEvent>> queue);

    void run() throws Exception ;

    void close();

}
