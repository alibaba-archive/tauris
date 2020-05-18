package com.aliyun.tauris.plugins.fomatter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TFormatter;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.hubspot.jinjava.Jinjava;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("jinja")
public class JinjaFormatter implements TFormatter {

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
		put("__time__", event.getTimestamp());
		put("__date__", new Date(event.getTimestamp()));
                put("__meta__", event.getMeta());
                putAll(event.getFields());
            }
        };
        return parser.render(template, data);
    }
}
