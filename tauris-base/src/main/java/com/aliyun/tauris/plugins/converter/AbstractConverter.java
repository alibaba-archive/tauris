package com.aliyun.tauris.plugins.converter;

import com.aliyun.tauris.TConverter;
import org.apache.commons.beanutils.ConvertUtils;

/**
 * Created by ZhangLei on 2018/4/28.
 */
public abstract class AbstractConverter implements TConverter {

    public AbstractConverter() {
        ConvertUtils.register(this, this.getType());
    }

}
