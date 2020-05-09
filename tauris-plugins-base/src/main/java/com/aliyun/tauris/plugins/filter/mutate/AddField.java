package com.aliyun.tauris.plugins.filter.mutate;

import com.alibaba.fastjson.JSON;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TLogger;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TResource;
import com.aliyun.tauris.formatter.EventFormatter;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ZhangLei on 16/12/14.
 */
public class AddField extends BaseMutate {

    private TLogger logger;

    /**
     * use fields insteed
     */
    @Deprecated
    Map<String, Object> newFields;

    Map<String, Object> fields;

    boolean formatKey;

    TResource resource;

    private Map<Object, Object> _fields = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    boolean overwrite = true;

    /**
     * add fields as meta of event
     */
    boolean meta = false;

    public void init() throws TPluginInitException {
        logger = TLogger.getLogger(this);
        if (resource != null) {
            try {
                byte[] df = resource.fetch();
                _fields.putAll(loadResource(df));
                this.resource.watch((b) -> {
                    Map<String, Object> nfields;
                    try {
                        nfields = loadResource(b);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    lock.lock();
                    _fields.clear();
                    _fields.putAll(nfields);
                    lock.unlock();
                });
            } catch (Exception e) {
                throw new TPluginInitException(e.getMessage(), e);
            }
        }
        initFields(fields);
        initFields(newFields);
    }

    private void initFields(Map<String, Object> fs) {
        if (fs != null) {
            Set<String> keySet = new HashSet<>(fs.keySet());
            for (String rawKey : keySet) {
                Object key = rawKey;
                Object rawVal = fs.get(rawKey);
                Object val = rawVal;

                if (formatKey && isExprValue(key)) {
                    key = EventFormatter.build(rawKey);
                }
                if (isExprValue(rawVal)) {
                    try {
                        val = EventFormatter.build((String) rawVal);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                this._fields.put(key, val);
            }
        }
    }

    private Map<String, Object> loadResource(byte[] data) throws TPluginInitException {
        String fn = new File((resource.getURI().getPath())).getName();
        if (!fn.endsWith(".json") && !fn.endsWith("yml") && !fn.endsWith(".yaml")) {
            throw new TPluginInitException(fn + " is unknown file type, json and yaml is supported");
        }
        if (data == null || data.length == 0) {
            return Collections.emptyMap();
        }
        if (fn.endsWith(".json")) {
            return loadJsonField(data);
        } else {
            return loadYamlField(data);
        }
    }

    private Map<String, Object> loadJsonField(byte[] data) {
        try {
            return JSON.parseObject(new String(data, "UTF-8"));
        } catch (Exception e) {
            throw new UnknownFormatConversionException(resource.toString() + " invalid json format");
        }
    }

    private Map<String, Object> loadYamlField(byte[] data) {
        Yaml   yaml       = new Yaml();
        Object yamlObject = yaml.load(new ByteArrayInputStream(data));
        if (!(yamlObject instanceof Map)) {
            throw new UnknownFormatConversionException(resource.toString() + " not key-value format");
        }
        Map<?, ?> yamlMap = (Map)yamlObject;
        Map<String, Object> nfields = new HashMap<>();
        for (Map.Entry ex: yamlMap.entrySet()) {
            nfields.put(ex.getKey().toString(), ex.getValue());
        }
        return nfields;
    }

    @Override
    public void mutate(TEvent event) {
        if (test(event)) {
            lock.lock();
            for (Map.Entry<Object, Object> entry : _fields.entrySet()) {
                Object key = entry.getKey();
                Object val = entry.getValue();
                if (key instanceof EventFormatter) {
                    key = ((EventFormatter) key).format(event);
                }
                if (val instanceof EventFormatter) {
                    val = ((EventFormatter) val).format(event);
                }
                if (overwrite || event.get((String) key) == null) {
                    try {
                        event.set((String) key, val);
                    } catch (NullPointerException e) {
                    }
                }
            }
            lock.unlock();
        }
    }

    private boolean isExprValue(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        String str = (String) value;
        return str.contains("%{");
    }

}
