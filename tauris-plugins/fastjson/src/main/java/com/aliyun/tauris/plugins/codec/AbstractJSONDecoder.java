package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;


/**
 * 将字符串encode为json对象
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class AbstractJSONDecoder extends AbstractDecoder {

    protected void copyObjectToEvent(TEvent event, JSONObject object) throws DecodeException {
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            event.set(entry.getKey(), entry.getValue());
        }
    }

    protected static Object getValueByKey(Map<String, Object> data, String name) {
        if (data.containsKey(name)) {
            return data.get(name);
        } else {
            String[] parts = name.split("\\.");
            Object v = data;
            int elementIndex = 0;
            int leftBracket;

            for (int i = 0; i < parts.length; i++) {
                boolean last = parts.length - 1 == i;
                String part = parts[i];
                boolean valueIsArray = false;

                if (part.charAt(part.length() - 1) == ']') {
                    valueIsArray = true;
                    leftBracket = part.lastIndexOf('[');
                    try {
                        elementIndex = Integer.parseInt(part.substring(leftBracket + 1, part.length() - 1));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    part = part.substring(0, leftBracket);
                }

                if (v instanceof Map) {
                    v = ((Map) v).get(part);
                    if (v == null) {
                        return null;
                    }
                } else {
                    try {
                        Method getter = v.getClass().getMethod("get" + StringUtils.capitalize(part));
                        v = getter.invoke(v);
                    } catch (Exception e) {
                        return null;
                    }
                }
                if (v == null) {
                    return null;
                }
                if (valueIsArray) {
                    try {
                        try {
                            if (v instanceof List) {
                                if (elementIndex < 0) {
                                    elementIndex = ((List) v).size() + elementIndex;
                                }
                                v = ((List) v).get(elementIndex);
                            } else if (v.getClass().isArray()) {
                                if (elementIndex < 0) {
                                    elementIndex = Array.getLength(v) + elementIndex;
                                }
                                v = Array.get(v, elementIndex);
                            } else {
                                return null;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            return null;
                        }

                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                if (last || v == null) {
                    return v;
                }
            }
            return v;
        }
    }

}

