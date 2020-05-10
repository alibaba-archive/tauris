package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

/**
 * Created by zhanglei on 2019/3/13.
 */
@Type(name = "formatter")
public interface TFormatter extends TPlugin {

    String format(TEvent event);

}
