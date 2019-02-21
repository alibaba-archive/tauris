package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.config.TConfigException;
import com.aliyun.tauris.utils.TProperty;

/**
 * Created by ZhangLei on 16/12/12.
 */
public abstract class Value {

    void assignTo(TProperty property) {
        try {
            _assignTo(property);
            Helper.m.append(this.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new TConfigException(e.getMessage());
        }
    }

    abstract void _assignTo(TProperty property) throws Exception;


}
