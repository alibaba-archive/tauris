package com.aliyun.tauris.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Type {
    /**
     * 类型名称
     * @return
     */
    String value() default "";

    /**
     * 类型名称
     * @return
     */
    String name() default "";

    /**
     * 是否可配置, 默认为true
     * 如果为false,在系统启动时会自动初始化一个全局实例.
     * @return
     */
    boolean configurable() default true;
}
