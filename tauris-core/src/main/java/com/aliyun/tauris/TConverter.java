package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;
import org.apache.commons.beanutils.Converter;

/**
 * Created by zhanglei on 2019/3/13.
 */
@Type(name = "converter", configurable = false)
public interface TConverter extends TPlugin, Converter {

    Class<?> getType();

}
