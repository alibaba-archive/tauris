package com.aliyun.tauris.plugins.scroll;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class ScrollRequest implements Scroll {

    private ScrollHeader header;

    private String body;

    public ScrollRequest(ScrollHeader header, String body) {
        this.header = header;
        this.body = body;
    }

    public ScrollHeader getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }
}
