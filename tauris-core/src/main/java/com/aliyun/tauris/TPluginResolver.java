package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Factory;
import com.aliyun.tauris.annotations.Name;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 16/10/25.
 */
public class TPluginResolver {

    public static TPluginResolver defaultResolver = new TPluginResolver();

    private Map<String, TPluginFactory> factories = new HashMap<>();

    public TPluginResolver() {
        ServiceLoader<TPluginFactory> fs = ServiceLoader.load(TPluginFactory.class);
        for (TPluginFactory f : fs) {
            String name = typeNameOfFactory(f.getClass());
            factories.put(name, f);
        }
    }

    public TPluginFactory resolvePluginFactory(String typeName) {
        TPluginFactory factory = factories.get(typeName);
        if (factory == null) {
            throw new IllegalArgumentException("unknown plugin type:" + typeName);
        }
        return factory;
    }

    public TPlugin resolvePlugin(Class<? extends TPlugin> type, String pluginName) {
        return resolvePlugin(typeName(type), pluginName);
    }

    public TPlugin resolvePlugin(String typeName, String pluginName) {
        TPluginFactory factory = factories.get(typeName);
        if (factory == null) {
            throw new IllegalArgumentException("unknown plugin type:" + typeName);
        }
        return factory.newInstance(pluginName);
    }

    public static Class<? extends TPlugin> resolvePluginType(Class<? extends TPlugin> clazz) {
        if (clazz.isInterface()) {
            for (Class inf : ClassUtils.getAllInterfaces(clazz)) {
                String n = clazz.getSimpleName();
                if (n.charAt(0) == 'T' && inf.equals(TPlugin.class)) {
                    return clazz;
                }
                for (Class sinf : ClassUtils.getAllInterfaces(inf)) {
                    Class spt = resolvePluginType(sinf);
                    if (spt != null) {
                        return spt;
                    }
                }
            }
        } else {
            for (Class inf : ClassUtils.getAllInterfaces(clazz)) {
                if (isPluginType(inf)) {
                    return inf;
                }
            }
        }
        return null;
    }

    public static boolean isPluginType(Class<? extends TPlugin> inf) {
        for (Class sinf : inf.getInterfaces()) {
            if (sinf.equals(TPlugin.class)) {
                return true;
            }
        }
        return false;
    }

    public static String typeNameOfFactory(Class<? extends TPluginFactory> c) {
        Factory n = c.getAnnotation(Factory.class);
        if (n != null) {
            Class<? extends TPlugin> pluginClass = n.value();
            return typeName(pluginClass);
        }

        String name = c.getSimpleName();
        Pattern p = Pattern.compile("^T([\\w\\d]+)Factory$");
        Matcher m = p.matcher(name);
        if (!m.matches()) {
            throw new IllegalArgumentException("invalid plugin factory name:" + c.getSimpleName());
        }
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(m.group(1));
    }

    public static String pluginName(Class<? extends TPlugin> clazz) {
        Name n = clazz.getAnnotation(Name.class);
        if (n != null) {
            return n.value();
        }
        String typeName = StringUtils.capitalize(typeName(resolvePluginType(clazz)));
        String name = clazz.getSimpleName();
        if (name.endsWith(typeName)) {
            name = name.substring(0, name.length() - typeName.length());
        }
        name = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(name);
        return name;
    }


    public static String typeName(Class<? extends TPlugin> clazz) {
        Name n = clazz.getAnnotation(Name.class);
        if (n != null) {
            return n.value();
        }
        String name = clazz.getSimpleName().substring(1);
        name = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(name);
        return name;
    }

}
