package com.aliyun.tauris;

/**
 * Created by ZhangLei on 17/5/23.
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
