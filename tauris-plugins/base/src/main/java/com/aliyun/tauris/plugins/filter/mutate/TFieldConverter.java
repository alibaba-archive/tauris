package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;

/**
 * Created by ZhangLei on 16/12/14.
 */
@Type
public interface TFieldConverter extends TPlugin {

    void convert(TEvent event);

}
