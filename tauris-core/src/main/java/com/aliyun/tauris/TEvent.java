package com.aliyun.tauris;

import java.util.*;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public interface TEvent extends TObject {

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
