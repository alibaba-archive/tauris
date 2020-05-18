package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Type;

import java.util.*;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class TPluginResolver {

    public static final String OPT_TAURIS_PLUGIN_DIRECTORY = "tauris.plugin.directory";
    public static final String ENV_TAURIS_PLUGIN_DIRECTORY = "T_PLUGINS";
    private static TPluginResolver resolver;

    private Map<String, Map<String, Entry>> pluginClassMap = new HashMap<>();

    public TPluginResolver(Map<String, Map<String, Entry>> pluginClassMap) {
        this.pluginClassMap = pluginClassMap;
    }

    public static void configure(TPluginScanner scanner) {
        if (resolver != null) {
            throw new IllegalStateException("TPluginResolver has been configured");
        }
        Map<String, Map<String, Entry>> pluginClassMap = new HashMap<>();

        for (Class<? extends TPlugin> pluginType : scanner.scanPluginTypes()) {
            String typeName = PluginTools.typeName(pluginType);
            Map<String, Entry> cmap = pluginClassMap.get(typeName);
            if (cmap == null) {
                cmap = new HashMap<>();
                pluginClassMap.put(typeName, cmap);
            }
            for (Class<? extends TPlugin> pc : scanner.scanPluginClasses(pluginType)) {
                String pluginName = PluginTools.pluginName(pc);
                if (!cmap.containsKey(pluginName)) {
                    if (!isConfigurable(pc)) {
                        try {
                            cmap.put(pluginName, new Entry(pc, pc.newInstance()));
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new VerifyError("plugin " + pluginName + " instantiation exception");
                        }
                    } else {
                        cmap.put(pluginName, new Entry(pc, null));
                    }
                }
            }
        }
        resolver = new TPluginResolver(pluginClassMap);
    }

    public static TPluginResolver resolver() {
        return Objects.requireNonNull(resolver, "resolver not configured");
    }

    public <T extends TPlugin> T resolve(Class<? extends T> type, String pluginName) throws TPluginNotFoundException {
       return (T)resolve(PluginTools.typeName(type), pluginName);
    }

    public TPlugin resolve(String typeName, String pluginName) throws TPluginNotFoundException{
        Map<String, Entry> ss = pluginClassMap.get(typeName);
        if (ss == null) {
            throw new TPluginNotFoundException(typeName, pluginName);
        }
        Entry en = ss.get(pluginName);
        if (en == null) {
            throw new TPluginNotFoundException(typeName, pluginName);
        }
        return en.instance();
    }

    public <T extends TPlugin> Set<Class<? extends T>> resolveSubTypes(Class<T> clazz) {
        String name = PluginTools.typeName(clazz);
        Map<String, Entry> ss = pluginClassMap.get(name);
        if (ss == null) {
            return Collections.emptySet();
        }
        Set<Class<? extends T>> subTypes = new HashSet<>();
        for (Entry e: ss.values()) {
            subTypes.add((Class<? extends T>)e.getType());
        }
        return subTypes;
    }

    private static boolean isConfigurable(Class<? extends TPlugin> clazz) {
        Class<?> c = clazz;
        while (c != null) {
            if (c.getAnnotation(Type.class) != null && c.getAnnotation(Type.class).configurable()) {
                return true;
            }
            for (Class<?> inf : c.getInterfaces()) {
                if (inf.getAnnotation(Type.class) != null && inf.getAnnotation(Type.class).configurable()) {
                    return true;
                }
            }
            c = c.getSuperclass();
        }
        return false;
    }

    private static class Entry {
        private Class<? extends TPlugin> clazz;
        private TPlugin                  instance;

        public Entry(Class<? extends TPlugin> clazz, TPlugin instance) {
            this.clazz = clazz;
            this.instance = instance;
        }

        public Class<? extends TPlugin> getType() {
            return clazz;
        }

        public TPlugin instance() {
            if (instance != null) {
                return instance;
            } else {
                try {
                    return clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new VerifyError("plugin instantiation exception");
                }
            }
        }
    }

}
