package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Name;
import com.google.common.base.CaseFormat;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Created by ZhangLei on 17/5/23.
 */
public class AbstractPluginFactory implements TPluginFactory {

    private Logger LOG = LoggerFactory.getLogger(AbstractPluginFactory.class);

    private final Class<? extends TPlugin> pluginType;

    private final Reflections reflections;

    public AbstractPluginFactory(Class<? extends TPlugin> pluginType) {
        this.pluginType = pluginType;
        this.reflections = new Reflections(pluginType.getPackage().getName(), pluginType.getClassLoader());
    }

    @Override
    public TPlugin newInstance(String pluginName) {
        for (Class<? extends TPlugin> c : reflections.getSubTypesOf(pluginType)) {
            if (pluginName.equals(pluginName(c))
                    || pluginName.equals(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, c.getSimpleName()))
                && !(c.isInterface() || Modifier.isAbstract(c.getModifiers()))) {
                try {
                    return c.newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new IllegalStateException("cannot create instance for " + c.getName(), e);
                }
            }
        }
        return newInstanceFromSL(pluginName);
    }

    @Override
    public Set<Class<? extends TPlugin>> getPluginClasses() {
        Set<Class<? extends TPlugin>> classes = new HashSet<>();
        for (Class<? extends TPlugin> c : reflections.getSubTypesOf(pluginType)) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) {
                continue;
            }
            classes.add(c);
        }
        return classes;
    }

    public TPlugin newInstanceFromSL(String pluginName) {
        ServiceLoader<? extends TPlugin> ploader = ServiceLoader.load(pluginType);
        for (TPlugin plugin : ploader) {
            String name = pluginName(plugin.getClass());
            if (name.equals(pluginName)) {
                LOG.info(String.format("create plugin instance %s/%s", pluginType.getName(), pluginName));
                return plugin;
            }
        }
        throw new IllegalArgumentException(String.format("plugin %s not found, type is %s", pluginName, pluginType.getName()));
    }

    public static String pluginName(Class<? extends TPlugin> clazz) {
        Name n = clazz.getAnnotation(Name.class);
        if (n != null) {
            return n.value();
        }

        int i = 0;
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

}
