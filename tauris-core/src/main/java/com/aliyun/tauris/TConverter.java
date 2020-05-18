package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;
import org.apache.commons.beanutils.Converter;

import javax.annotation.PostConstruct;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type(name = "converter", configurable = false)
public interface TConverter extends TPlugin, Converter {

    Class<?> getType();

}
