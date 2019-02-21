package com.aliyun.tauris.utils;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.util.ServiceLoader;

/**
 * Created by ZhangLei on 2018/4/28.
 */
public abstract class TConverter implements Converter {

    public static void register() {
        ServiceLoader<? extends TConverter> cvt = ServiceLoader.load(TConverter.class);
        for (TConverter c : cvt) {
            ConvertUtils.register(c, c.getType());
        }
    }

    public abstract Class<?> getType();
}
