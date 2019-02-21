package com.aliyun.tauris.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ZhangLei on 16/10/25.
 */
public class EntryTree<T>{


    private char sep = '/';

    private Map<String, T> tree = new ConcurrentHashMap<>();

    public EntryTree() {
    }

    public EntryTree(char sep) {
        this.sep = sep;
    }

    public void add(String path, T e) {
        tree.put(path, e);
    }

    public List<T> list(String path) {
        List<T> ts = new ArrayList<>();
        for (String k : tree.keySet()) {
            if (k.startsWith(path + "/")) {
                ts.add(tree.get(k));
            }
        }
        return ts;
    }

}
