package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Name;
import com.google.common.base.CaseFormat;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by ZhangLei on 17/5/23.
 */
public class AbstractPluginFactory implements TPluginFactory {

    private Logger logger = LoggerFactory.getLogger(AbstractPluginFactory.class);

    private final Class<? extends TPlugin> pluginType;

    private final Reflections reflections;

    public AbstractPluginFactory(Class<? extends TPlugin> pluginType) {
        this.pluginType = pluginType;
        this.reflections = new Reflections(pluginType.getPackage().getName(), pluginType.getClassLoader());
    }

    @Override
    public TPlugin newInstance(String majorName, String minorName) {
        Class<? extends TPlugin>                  preferred = null;
        Map<PluginName, Class<? extends TPlugin>> matched   = new HashMap<>();
        try {
            for (Class<? extends TPlugin> c : reflections.getSubTypesOf(pluginType)) {
                if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) {
                    continue;
                }
                PluginName name = pluginName(c);
                if (name.major.equals(majorName)) {
                    if (name.minior.equals(minorName)) {
                        return c.newInstance();
                    }
                    matched.put(name, c);
                    if (name.preferred) {
                        preferred = c;
                    }
                }
            }
            if (preferred != null) {
                return preferred.newInstance();
            }
            if (!matched.isEmpty()) {
                return new ArrayList<>(matched.values()).get(0).newInstance();
            }
            return newInstanceFromSL(new PluginName(majorName, minorName));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(String.format("cannot create instance for %s", new PluginName(majorName, minorName)), e);
        }
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

    /**
     * 使用serviceloader创建插件实例
     * 优先返回插件的major名和和minor名与参数完全一致的插件
     * 其次返回major名与参数一致，且preferred为true的插件
     * 最后任意返回一个major名与参数一致的插件
     *
     * @param pluginName 插件名
     * @return plugin实例
     */
    public TPlugin newInstanceFromSL(PluginName pluginName) {
        logger.info(String.format("create plugin instance %s/%s", pluginType.getName(), pluginName));
        TPlugin                  preferred = null;
        Map<PluginName, TPlugin> matched   = new HashMap<>();

        ServiceLoader<? extends TPlugin> ploader = ServiceLoader.load(pluginType);
        for (TPlugin plugin : ploader) {
            PluginName name = pluginName(plugin.getClass());
            if (name.equals(pluginName)) {
                return plugin;
            }
            if (name.major.equals(pluginName.major)) {
                matched.put(name, plugin);
                if (name.preferred) {
                    preferred = plugin;
                }
            }
        }
        if (preferred != null) {
            return preferred;
        }
        if (!matched.isEmpty()) {
            return new ArrayList<>(matched.values()).get(0);
        }
        throw new IllegalArgumentException(String.format("plugin %s not found, type is %s", pluginName, pluginType.getName()));
    }

    public static PluginName pluginName(Class<? extends TPlugin> clazz) {
        Name n = clazz.getAnnotation(Name.class);
        if (n != null) {
            return new PluginName(n.value(), n.minor(), n.preferred());
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
        return new PluginName(name);
    }

    private static class PluginName {

        private final String  major;
        private final String  minior;
        private final boolean preferred;

        public PluginName(String major) {
            this.major = major;
            this.minior = "default";
            this.preferred = true;
        }

        public PluginName(String major, String minior) {
            this.major = major;
            this.minior = minior;
            this.preferred = false;
        }

        public PluginName(String major, String minior, boolean preferred) {
            this.major = major;
            this.minior = minior;
            this.preferred = preferred;
        }

        public String getMajor() {
            return major;
        }

        public String getMinior() {
            return minior;
        }

        public boolean isPreferred() {
            return preferred;
        }

        @Override
        public int hashCode() {
            return major.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PluginName)) {
                return false;
            }
            return major.equals(((PluginName) obj).major) && minior.equals(((PluginName) obj).minior);
        }

        @Override
        public String toString() {
            return minior.isEmpty() || minior.equals("default") ? major : String.format("%s.%s", major, minior);
        }
    }
}
