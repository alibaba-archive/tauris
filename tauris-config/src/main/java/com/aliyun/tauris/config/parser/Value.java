package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.config.TConfigException;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class Value {

    void assignTo(PluginProperty property) {
        try {
            _assignTo(property);
            Helper.m.append(this.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new TConfigException(e.getMessage());
        }
    }

    abstract void _assignTo(PluginProperty property) throws Exception;


}
