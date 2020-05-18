package com.aliyun.tauris.config;

/**
 * @author Ray Chaung<rockis@gmail.com>
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
