package com.aliyun.tauris;

import com.alibaba.texpr.Context;

import java.util.*;

/**
 * Created by ZhangLei on 16/12/8.
 */
public interface TEvent extends TObject, Context {

    String META_SOURCE    = "@source";
    String META_TIMESTAMP = "@timestamp";

    void addMeta(String key, Object value);

    Map<String, Object> getMeta();

    Object getMeta(String name);

    boolean contains(String name);

    void set(String name, Object value);

    Object get(String name);

    Object remove(String name);

    void setField(String name, Object value);

    Object removeField(String name);

    void setFields(Map<String, Object> fields);

    Map<String, Object> getFields();

    long getTimestamp();

    void setTimestamp(long timestamp);

    String getSource();

    void setSource(String source);

    void destroy();
}
