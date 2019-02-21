package com.aliyun.tauris.config;

/**
 * Created by ZhangLei on 17/5/14.
 */
public class TConfigText implements TConfig {

    private String text;

    public TConfigText(String text) {
        this.text = text;
    }

    @Override
    public String load() throws TConfigException {
        return text;
    }
}
