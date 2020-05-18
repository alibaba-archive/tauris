package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Type;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type
public interface TMutate extends TPlugin {

    void mutate(TEvent event);

}
