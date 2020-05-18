package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type(name = "formatter")
public interface TFormatter extends TPlugin {

    String format(TEvent event);

}
