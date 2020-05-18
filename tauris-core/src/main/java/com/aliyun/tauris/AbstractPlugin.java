package com.aliyun.tauris;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class AbstractPlugin implements TPlugin {

    protected String id;

    @Override
    public String id() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
