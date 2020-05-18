package com.aliyun.tauris.plugins.output.dingtalk;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;

import java.util.IllegalFormatException;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type
public interface TDingMessage extends TPlugin {
    String format(TEvent event) throws IllegalFormatException;
}
