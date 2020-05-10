package com.aliyun.tauris.plugins.scroll;

import java.util.Date;

/**
 * Created by ZhangLei on 16/10/22.
 */
public class ScrollHeader implements Scroll {

    private String hostname;
    private String appName;
    private String token;
    private String version;

    public ScrollHeader() {
    }

    public ScrollHeader(String hostname, String appName, String token, String version) {
        this.hostname = hostname;
        this.appName = appName;
        this.token = token;
        this.version = version;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format("version:%s,app-name:%s,token:%s,hostname:%s", version, appName, token, hostname);
    }
}
