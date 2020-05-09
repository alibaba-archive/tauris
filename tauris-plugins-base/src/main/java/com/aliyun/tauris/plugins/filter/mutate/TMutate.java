package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPlugin;

/**
 * Created by ZhangLei on 16/12/14.
 */
public interface TMutate extends TPlugin {

    void mutate(TEvent event);
}
