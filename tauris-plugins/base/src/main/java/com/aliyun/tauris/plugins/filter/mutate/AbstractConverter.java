package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.beanutils.ConvertUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class AbstractConverter implements TFieldConverter {

    @Required
    String[] fields;

    protected Class<?> targetType;

    protected Pattern pattern;

    int cacheSize = 0;

    int cacheExpiredSeconds = 0;

    private LoadingCache<Object, Optional<Object>> _cache = null;

    public AbstractConverter(Class<?> targetType) {
        this.targetType = targetType;
    }

    public AbstractConverter(Class<?> targetType, Pattern pattern) {
        this.targetType = targetType;
        this.pattern = pattern;
    }

    public void init() {
        if (cacheSize > 0) {
            CacheBuilder<Object, Object> b = CacheBuilder.newBuilder().maximumSize(cacheSize);
            if (cacheExpiredSeconds > 0) {
                b.expireAfterAccess(cacheExpiredSeconds, TimeUnit.SECONDS);
            }
            _cache = b.build(new CacheLoader<Object, Optional<Object>>() {
                        public Optional<Object> load(Object key) {
                            return convert(key);
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
                        Optional<Object> opt = convert(value);
                        if (opt.isPresent()) {
                            event.set(field, opt.get());
                        }
                    } catch (Exception ex) {
                        event.remove(field);
                    }
                } else {
                    try {
                        Optional<Object> opt = _cache.get(value);
                        if (opt.isPresent()) {
                            event.set(field, opt.get());
                        } else {
                            event.remove(field);
                        }
                    } catch (Exception ex) {
                        event.remove(field);
                    }
                }
            }
        }
    }

    protected Optional<Object> convert(Object value) {
        if (value == null) {
            return Optional.empty();
        }
        if (pattern != null && !pattern.matcher(value.toString()).matches()) {
            return Optional.empty();
        }
        return Optional.of(ConvertUtils.convert(value, targetType));
    }


}
