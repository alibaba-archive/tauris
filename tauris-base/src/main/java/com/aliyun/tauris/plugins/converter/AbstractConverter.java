package com.aliyun.tauris.plugins.converter;

import com.aliyun.tauris.TConverter;
import org.apache.commons.beanutils.ConvertUtils;

import javax.annotation.PostConstruct;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class AbstractConverter implements TConverter {

    @PostConstruct
    public void register() {
        ConvertUtils.register(this, this.getType());
    }
}
