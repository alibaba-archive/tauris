package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TPluginResolver;

/**
 * codec => debug {
 *            path => '/metrics';
 *            acl  => '*';
 *          }
 * Created by ZhangLei on 16/12/14.
 */
class PluginValue extends Value {

    private final Plugin plugin;

    public PluginValue(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    void _assignTo(TProperty property) throws Exception {
        //此属性是一个TPlugin对象
        Helper.m.expand().next();
        //resolvePlugin(com.aliyun.tauris.TEncoder, plugin.name)
        TPlugin instance = TPluginResolver.defaultResolver.resolvePlugin((Class<? extends TPlugin>)property.getType(), plugin.getMajorName(), plugin.getMinorName());
        plugin.build(instance);
        property.set(instance);
        Helper.m.collapse();
    }

    @Override
    public String toString() {
        return "";
    }
}
