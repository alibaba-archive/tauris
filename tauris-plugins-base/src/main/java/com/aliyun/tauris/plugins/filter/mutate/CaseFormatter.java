package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.google.common.base.CaseFormat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 转换event的key或value的大小写
 * Created by ZhangLei on 16/12/14.
 */
@Name("caseformat")
public class CaseFormatter implements TMutate {

    public enum Mode {
        key,  //转换key的大小写
        value //转换value的大小写
    }

    @Required
    CaseFormat from;

    @Required
    CaseFormat to;

    Mode mode = Mode.key;

    String[] fields;

    private Map<String, String> _keyMap = new HashMap<>();

    public void init() {
        if (mode == Mode.key && fields != null) {
            for (String f : fields) {
                _keyMap.put(f, from.to(to, f));
            }
        }
    }

    @Override
    public void mutate(TEvent event) {
        if (mode == Mode.key) {
            if (_keyMap.isEmpty()) {
                Set<String> ks = new HashSet<>(event.getFields().keySet());
                for (String k : ks) {
                    Object v = event.get(k);
                    String nk = from.to(to, k);
                    event.remove(k);
                    event.set(nk, v);
                }
            } else {
                for (String k : _keyMap.keySet()) {
                    Object val = event.get(k);
                    if (val != null) {
                        event.remove(k);
                        event.set(_keyMap.get(k), val);
                    }
                }
            }
        } else {
            if (fields == null) {
                for (String k: event.getFields().keySet()) {
                    Object v = event.get(k);
                    if (v instanceof String) {
                        event.set(k, from.to(to, ((String)v)));
                    }
                }
            } else {
                for (String k: fields) {
                    Object v = event.get(k);
                    if (v != null) {
                        event.set(k, from.to(to, ((String)v)));
                    }
                }
            }
        }
    }
}
