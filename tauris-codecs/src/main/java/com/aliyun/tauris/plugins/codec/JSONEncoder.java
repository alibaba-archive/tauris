package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.TEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;


/**
 * 将对象encode为字符串
 * Created by ZhangLei on 16/12/7.
 */
@Name("json")
public class JSONEncoder extends AbstractEncoder {

    String[] includes;

    String[] excludes;

    boolean pretty;

    private SimplePropertyPreFilter filter;

    @Override
    public void init() {
        if (excludes == null && includes == null) {
            return;
        }
        filter = new SimplePropertyPreFilter();
        if (excludes != null) {
            filter.getExcludes().addAll(Arrays.asList(excludes));
        }
        if (includes != null) {
            filter.getIncludes().addAll(Arrays.asList(includes));
        }
    }

    @Override
    public void encode(TEvent event, String target) throws EncodeException {
        String json;
        Map<String, Object> fields = event.getFields();
        if (filter == null) {
            json = pretty ? JSON.toJSONString(fields, SerializerFeature.PrettyFormat) : JSON.toJSONString(fields);
        } else {
            json = pretty ? JSON.toJSONString(fields, filter, SerializerFeature.PrettyFormat) : JSON.toJSONString(fields, filter);
        }
        event.set(target, json);
    }

    @Override
    public void encode(TEvent event, OutputStream output) throws EncodeException, IOException {
        try {
            SerializeWriter sw;
            if (pretty) {
                sw = new SerializeWriter(new OutputStreamWriter(output), JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.PrettyFormat);
            } else {
                sw = new SerializeWriter(new OutputStreamWriter(output));
            }
            JSONSerializer serializer = new JSONSerializer(sw);
            if (filter != null) {
                serializer.addFilter(filter);
            }
            serializer.write(event.getFields());
            sw.close();
        } catch (JSONException e) {
            throw new EncodeException("json encode error", e);
        }
    }

}

