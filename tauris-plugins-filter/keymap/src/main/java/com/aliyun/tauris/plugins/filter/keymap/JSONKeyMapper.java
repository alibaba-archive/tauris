package com.aliyun.tauris.plugins.filter.keymap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.aliyun.tauris.TObject;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.alibaba.texpr.TExpression;
import com.aliyun.tauris.TLogger;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangLei on 17/7/27.
 */
@Name("json")
public class JSONKeyMapper extends AbstractResourceKeyMapper {

    private TLogger logger;

    @Required
    String keyName;

    String valueName;

    TExpression filter;

    public JSONKeyMapper() {
        this.logger = TLogger.getLogger(this);
    }

    @Override
    public void update(String text) {
        Map<String, Object> data    = new HashMap<>();
        JSONObjectWrapper   wrapper = new JSONObjectWrapper();
        for (String line : text.split("\n")) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            try {
                Object jo = JSON.parse(line);
                if (jo instanceof JSONObject) {
                    JSONObject o = (JSONObject) jo;
                    if (!o.containsKey(keyName)) {
                        logger.WARN("key " + keyName + " not found in " + line);
                    } else {
                        if (filter != null) {
                            wrapper.wrap(o);
                            if (!filter.check(wrapper)) {
                                continue;
                            }
                        }
                        data.put(o.getString(keyName), jo);
                    }
                } else {
                    logger.WARN("parse json object failed, source is: " + line);
                }
            } catch (Exception e) {
                logger.WARN("parse json failed, source is: " + line, e);
            }
        }
        if (data.isEmpty() && !mapping.isEmpty()) {
            logger.ERROR("the new resource is empty from " + resource.getURI() + "");
        } else {
            update(data);
        }
    }

    @Override
    public Object resolveValue(Object value) {
        if (valueName == null) {
            return value;
        }
        JSONObject jo = (JSONObject)value;
        return JSONPath.eval(jo, valueName);
    }

    static class JSONObjectWrapper implements TObject {

        JSONObject object;

        void wrap(JSONObject object) {
            this.object = object;
        }

        @Override
        public Object get(String name) {
            if (name.isEmpty()) {
                return null;
            }
            if (name.charAt(0) == '$') {
                return object.get(name.substring(1));
            }
            return object.get(name);
        }
    }
}
