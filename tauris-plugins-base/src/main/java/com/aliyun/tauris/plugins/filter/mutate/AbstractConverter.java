package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.beanutils.ConvertUtils;

import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 16/12/14.
 */
public abstract class AbstractConverter implements TConverter {

    @Required
    String[] fields;

    protected Class<?> targetType;

    protected Pattern pattern;

    int cacheSize = 0;

    private LoadingCache<Object, Nullable> _cache = null;

    public AbstractConverter(Class<?> targetType) {
        this.targetType = targetType;
    }

    public AbstractConverter(Class<?> targetType, Pattern pattern) {
        this.targetType = targetType;
        this.pattern = pattern;
    }

    public void init() {
        if (cacheSize > 0) {
            _cache = CacheBuilder.newBuilder().maximumSize(cacheSize)
                    .build(new CacheLoader<Object, Nullable>() {
                        public Nullable load(Object key) {
                            return new Nullable(convert(key));
                        }
                    });
        }
    }

    public void convert(TEvent event) {
        for (String field : fields) {
            Object value = event.get(field);
            if (value != null) {
                if (_cache == null) {
                    try {
                        value = convert(value);
                        event.set(field, value);
                    } catch (Exception ex) {
                        event.remove(field);
                    }
                } else {
                    try {
                        Nullable v = _cache.get(value);
                        if (v.isNull()) {
                            event.remove(field);
                        } else {
                            event.set(field, v.getValue());
                        }
                    } catch (Exception ex) {
                        event.remove(field);
                    }
                }
            }
        }
    }

    protected Object convert(Object value) {
        if (value == null) {
            return null;
        }
        if (pattern != null && !pattern.matcher(value.toString()).matches()) {
            return null;
        }
        return ConvertUtils.convert(value, targetType);
    }

    private static class Nullable {

        private Object value;

        public Nullable(Object value) {
            this.value = value;
        }

        public boolean isNull() {
            return this.value == null;
        }

        public Object getValue() {
            return value;
        }
    }


}
