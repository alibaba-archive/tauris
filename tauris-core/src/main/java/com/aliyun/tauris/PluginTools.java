package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Type;
import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class PluginTools
 *
 * @author ZhangLei
 * @date 2018-09-12
 */
public class PluginTools {


    /**
     * 释放资源
     */
    public static void release(TPlugin plugin) {
        release(plugin, plugin.getClass());
    }

    /**
     * 释放资源
     */
    public static void release(TPlugin plugin, Class<?> clazz) {
        if (clazz.getSuperclass() != null) {
            release(plugin, clazz.getSuperclass());
        }
        plugin.release();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field: fields) {
            try {
                if (TPlugin.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    TPlugin pluginField = (TPlugin) field.get(plugin);
                    if (pluginField != null) {
                        release(pluginField);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void pluginInit(Object o) throws IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader pluginClassLoader  = o.getClass().getClassLoader();
        Thread.currentThread().setContextClassLoader(pluginClassLoader);
        Method initMethod = o.getClass().getMethod("init");
        initMethod.invoke(o);
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

    public static String pluginName(Class<? extends TPlugin> clazz) {
        Name n = clazz.getAnnotation(Name.class);
        if (n != null) {
            return n.value();
        }

        int    i  = 0;
        char[] cs = clazz.getSimpleName().toCharArray();
        for (i = cs.length - 1; i >= 0; i--) {
            if (Character.isUpperCase(cs[i])) {
                break;
            }
        }
        String name = clazz.getSimpleName().substring(0, i);
        name = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(name);
        return name;
    }

    public static String typeName(Class<? extends TPlugin> clazz) {
        Type n = clazz.getAnnotation(Type.class);
        if (n != null) {
            String name = n.value().trim().isEmpty() ? n.name() : n.value();
            if (!name.isEmpty()) {
                return name;
            }
        }

        String name = clazz.getSimpleName();
        if (name.length() < 2) {
            throw new IllegalArgumentException("invalid plugin class name:" + name);
        }
        char firstChar  = name.charAt(0);
        char secondChar = name.charAt(1);
        if (firstChar == 'T' && secondChar > 'A') {
            name = name.substring(1);
        }
        name = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(name);
        return name;
    }
}
