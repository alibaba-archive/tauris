package com.aliyun.tauris.plugins.output.dingtalk;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPlugin;

import java.util.IllegalFormatException;

/**
 * Created by ZhangLei on 2018/5/17.
 */
public interface TDingMessage extends TPlugin {
    String format(TEvent event) throws IllegalFormatException;
}
