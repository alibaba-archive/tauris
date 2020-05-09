package com.aliyun.tauris.config.parser;

import org.apache.commons.beanutils.ConvertUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangLei on 16/12/12.
 */
public class HashValue extends Value {

    private List<KeyValue> elements;

    private String repr;

    public HashValue(List<KeyValue> elements) {
        this.elements = elements;
    }

    @Override
    void _assignTo(TProperty property) throws Exception {
        Helper m = new Helper();
        Map<String, Object> map = new HashMap<>();
        m.expand("{").next();
        for (KeyValue e : elements) {
            if (property.getValueType() != null) {
                map.put(e.getKey().value, ConvertUtils.convert(e.value(), property.getValueType()));
            } else {
                map.put(e.getKey().value, e.value());
            }
            m.message(String.format("\"%s\"", e.getKey().value)).append(":");

            Object val = e.value();
            if (val == null) {
                m.append("null").trim().text(",").next();
            } else {
                m.append(e.value().toString()).text(",").next();
            }
        }
        m.collapse("}").next();
        property.set(map);
        repr = m.toString();
    }

    @Override
    public String toString() {
        return repr;
    }
}
