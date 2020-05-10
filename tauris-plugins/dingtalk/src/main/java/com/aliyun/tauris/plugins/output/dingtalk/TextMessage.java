package com.aliyun.tauris.plugins.output.dingtalk;

import com.alibaba.fastjson.JSON;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.formatter.SimpleFormatter;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

/**
 * Created by ZhangLei on 2018/5/17.
 */
@Name("text")
public class TextMessage implements TDingMessage {

    @Required
    SimpleFormatter content;

    boolean atAll;

    String[] atMobiles;

    @Override
    public String format(TEvent event) throws IllegalFormatException {
        String              cnt = content.format(event);
        Map<String, Object> msg = new HashMap<>();
        msg.put("msgtype", "text");

        Map<String, Object> text = new HashMap<>();
        text.put("content", cnt);
        msg.put("text", text);

        Map<String, Object> at = new HashMap<>();
        at.put("isAtAll", atAll);
        if (atMobiles != null) {
            at.put("atMobiles", atMobiles);
        }
        msg.put("at", at);
        return JSON.toJSONString(msg);
    }
}
