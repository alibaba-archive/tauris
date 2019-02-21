package com.aliyun.tauris.resource;

import com.aliyun.tauris.TResource;
import com.aliyun.tauris.utils.TConverter;

/**
 * Created by ZhangLei on 2018/4/28.
 */
public class TResourceConverter extends TConverter {

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
