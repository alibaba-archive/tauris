package com.aliyun.tauris.plugins.output.dingtalk;

import com.alibaba.fastjson.JSON;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.hubspot.jinjava.Jinjava;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

/**
 * Created by ZhangLei on 2018/5/17.
 */
@Name("markdown")
public class MarkdownMessage implements TDingMessage {

    private static Logger logger = LoggerFactory.getLogger(MarkdownMessage.class);

    @Required
    String title;

    String text;

    TResource template;

    boolean atAll;

    String[] atMobiles;

    private Jinjava parser = new Jinjava();

    public void init() throws TPluginInitException {
        if (text == null && template == null) {
            throw new TPluginInitException("text or template must be set");
        }
        try {
            if (template != null) {
                text = new String(template.fetch());
                template.watch((c) -> {
                    try {
                        text = new String(template.fetch());
                    } catch (Exception e) {
                        logger.error("resource " + template.getURI().toString() + " has an error", e);
                    }
                });
            }
        } catch (Exception e) {
            throw new TPluginInitException("template error", e);
        }
    }

    @Override
    public String format(TEvent event) throws IllegalFormatException {
        HashMap<String, Object> data = new HashMap<String, Object>() {
            {
                put("__time__", event.getTimestamp().toDate());
                put("__meta__", event.getMeta());
                putAll(event.getFields());
            }
        };

        Map<String, Object> msg = new HashMap<>();
        msg.put("msgtype", "markdown");

        Map<String, Object> md = new HashMap<>();
        md.put("title", parser.render(title, data));
        md.put("text", parser.render(text, data));

        msg.put("markdown", md);

        Map<String, Object> at = new HashMap<>();
        at.put("isAtAll", atAll);
        if (atMobiles != null) {
            at.put("atMobiles", atMobiles);
        }
        msg.put("at", at);
        return JSON.toJSONString(msg);
    }
}
