package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 将event转换为字符串, 用于output组件
 * Created by ZhangLei on 16/12/7.
 */
@Type
public interface TEncoder extends TPlugin {

    default void init() {}

    void encode(TEvent event, String target) throws EncodeException;

    String encode(TEvent event) throws EncodeException;

    void encode(TEvent event, OutputStream out) throws EncodeException, IOException;
}
