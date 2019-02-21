package com.aliyun.tauris.formatter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.hubspot.jinjava.Jinjava;

import java.util.HashMap;

/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("jinja")
public class JinjaFormatter implements TEventFormatter {

    TResource templateURI;

    String template;

    private Jinjava parser = new Jinjava();

    public void init() throws TPluginInitException  {
        if (template == null && templateURI == null) {
            throw new TPluginInitException( "template or template_uri is required");
        }
        if (templateURI != null) {
            try {
                template = new String(templateURI.fetch(), "UTF-8");
            } catch (Exception e) {
                throw new TPluginInitException( "fetch template failed", e);
            }
        }
    }

    @Override
    public String format(TEvent event) {
        HashMap<String, Object> data = new HashMap<String, Object>() {
            {
                put("__time__", event.getTimestamp().toDate());
                put("__meta__", event.getMeta());
                putAll(event.getFields());
            }
        };
        return parser.render(template, data);
    }
}
