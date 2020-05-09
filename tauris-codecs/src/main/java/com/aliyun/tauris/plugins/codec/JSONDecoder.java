package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TEventFactory;
import com.aliyun.tauris.annotations.Name;

import javax.annotation.Nullable;


/**
 * 将字符串encode为json对象
 * Created by ZhangLei on 16/12/7.
 */
@Name("json")
public class JSONDecoder extends AbstractJSONDecoder {

    String[] metaKeys;
    String   subKey;

    private MetaSetter[] metaSetters;

    public void init() {
        if (metaKeys != null) {
            metaSetters = new MetaSetter[metaKeys.length];
            for (int i = 0; i < metaKeys.length; i++) {
                metaSetters[i] = new MetaSetter(metaKeys[i]);
            }
        }
    }

    @Override
    public TEvent decode(String source, TEventFactory factory) throws DecodeException {
        JSONObject object = parse(source);
        TEvent event = factory.create(source);
        decodeToEvent(event, object);
        return event;
    }

    @Override
    public void decode(String source, TEvent event, @Nullable String target) throws DecodeException {
        JSONObject object = parse(source);
        if (target != null) {
            decodeToTarget(event, object, target);
        } else {
            decodeToEvent(event, object);
        }
    }

    private void setupMeta(TEvent event, JSONObject object) {
        if (metaSetters != null) {
            for (MetaSetter setter: metaSetters) {
                setter.set(event, object);
            }
        }
    }

    public void decodeToTarget(TEvent event, JSONObject object, String target) throws DecodeException {
        setupMeta(event, object);
        if (subKey != null) {
            Object sub = getValueByKey(object, subKey);
            if (sub == null) {
                throw new DecodeException("sub key is null");
            }
            event.set(target, sub);
        } else {
            event.set(target, object);
        }
    }

    private void decodeToEvent(TEvent event, JSONObject object) throws DecodeException{
        setupMeta(event, object);
        copyObjectToEvent(event, object);
    }

    private JSONObject parse(String text) throws DecodeException {
        if (text == null || text.length() < 2) {
            throw new DecodeException("source is empty");
        }
        try {
            return JSON.parseObject(text);
        } catch (JSONException e) {
            System.err.println(text);
            throw new DecodeException("json decode error", e, text);
        }
    }

    private static class MetaSetter {

        private String mapKey;
        private String metaName;

        public MetaSetter(String key) {
            this.mapKey = key;
            this.metaName = key;
            String[] ks = key.split(":"); // left is key of object, right is tag name of event
            if (ks.length == 2) {
                this.mapKey = ks[0];
                this.metaName = ks[1];
            } else {
                int lastDot = mapKey.lastIndexOf('.');
                if (lastDot > 0) {
                    this.metaName = mapKey.substring(lastDot + 1);
                }
            }
        }

        public void set(TEvent event, JSONObject object) {
            Object tagVal = getValueByKey(object, mapKey);
            if (tagVal == null) {
                return;
            }
            event.addMeta(metaName, tagVal);
        }
    }
}

