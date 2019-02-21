package com.aliyun.tauris;

import java.lang.reflect.Field;

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

}
