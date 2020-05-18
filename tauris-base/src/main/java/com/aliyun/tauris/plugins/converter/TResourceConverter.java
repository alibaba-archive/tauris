package com.aliyun.tauris.plugins.converter;

import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.plugins.converter.AbstractConverter;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("resource")
public class TResourceConverter extends AbstractConverter {

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("cannot convert " + value.getClass() + " to TResource");
        }
        String strVal = (String)value;
        return (T) TResource.valueof(strVal);
    }

    @Override
    public Class<?> getType() {
        return TResource.class;
    }
}
