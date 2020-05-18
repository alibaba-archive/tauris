package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TPluginResolver;

import java.lang.reflect.Array;
import java.util.List;

/**
 * servlets => {
 *     metrics {
 *         path => '/metrics';
 *         acl  => '*';
 *     }
 *  }
 * @author Ray Chaung<rockis@gmail.com>
 */
class PluginsValue extends Value {

    private final List<Plugin> plugins;

    public PluginsValue(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    void _assignTo(PluginProperty property) throws Exception {
        //此属性是一个TPlugin数组
        Helper.m.expand("{").next();
        Object array = Array.newInstance(property.getType(), plugins.size());
        int    i     = 0;
        for (Plugin e : plugins) {
            TPlugin plugin = TPluginResolver.resolver().resolve((Class<? extends TPlugin>)property.getType(), e.getName());
            plugin = e.marshal(plugin);
            Array.set(array, i, plugin);
            i++;
        }
        property.set(array);
        Helper.m.collapse("}").next();
    }

    @Override
    public String toString() {
        return "";
    }
}
