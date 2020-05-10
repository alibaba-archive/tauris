package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.*;
import com.aliyun.tauris.config.TConfigException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by jdziworski on 30.03.16.
 */
public class Plugin {

    /**
     * 插件名称, 如 stdin
     */
    private String      name;
    private Assignments assignments;

    public Plugin(String name, Assignments assignments) {
        this.name = name;
        this.assignments = assignments;
    }

    public String getName() {
        return name;
    }

    public TPlugin marshal(TPlugin plugin) {
        Helper.m.message(name).expand("{").next();
        this.assignments.assignTo(plugin);
        String pid = PluginId.generateId(name);
        if (plugin.id() == null) {
            plugin.setId(pid);
            Helper.m.collapse("} //id: " + pid).next();
        } else {
            Helper.m.collapse("}").next();
        }
        init(plugin, name);
        return plugin;
    }

    private void init(Object o, String name) {
        try {
            PluginTools.pluginInit(o);
        } catch (IllegalArgumentException e) {
            throw new TConfigException("init component " + name + " failed, cause by " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            // ignore
        } catch (IllegalAccessException e) {
            System.err.println("warning: cannot access init method of " + o.getClass());
        } catch (InvocationTargetException e) {
            Throwable source = e.getTargetException();
            throw new TConfigException("init component " + name + " failed, cause by " + source.getMessage(), source);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plugin component = (Plugin) o;

        if (name != null ? !name.equals(component.name) : component.name != null) return false;
        return assignments != null ? assignments.equals(component.assignments) : component.assignments == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (assignments != null ? assignments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Method{" +
                "\nname='" + name + '\'' +
                "\nassignments=" + assignments +
                '}';
    }
}
