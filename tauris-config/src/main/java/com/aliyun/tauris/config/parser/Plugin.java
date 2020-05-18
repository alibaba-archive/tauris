package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.*;
import com.aliyun.tauris.config.TConfigException;

import java.lang.reflect.InvocationTargetException;


/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class Plugin {
    private String      majorName;
    private String      minorName;
    private Assignments assignments;

    public Plugin(String majorName, String minorName, Assignments assignments) {
        this.majorName = majorName;
        this.minorName = minorName;
        this.assignments = assignments;
    }

    public String getName() {
        return minorName == null || minorName.equals("default") ? majorName : String.format("%s.%s", majorName, minorName);
    }

    public String getMajorName() {
        return majorName;
    }

    public String getMinorName() {
        return minorName;
    }

    public TPlugin marshal(TPlugin plugin) {
        Helper.m.message(minorName == null ? majorName : String.format("%s.%s", majorName, minorName)).expand("{").next();
        this.assignments.assignTo(plugin);
        if (plugin.id() == null) {
            String pid = PluginId.generateId(PluginTools.pluginName(plugin.getClass()));
            plugin.setId(pid);
            Helper.m.collapse("} # id: " + pid).next();
        } else {
            Helper.m.collapse("}").next();
        }
        init(plugin);
        return plugin;
    }

//    public <T extends TPlugin> TPlugin build(Class<T> type) {
//        TPluginResolver resolver = TPluginResolver.resolver();
//        TPlugin         plugin   = resolver.resolvePlugin(type, majorName, minorName);
//        return this.build(plugin);
//        return null;
//    }

//    public TPlugin build(TPluginFactory factory) {
//        TPlugin plugin = factory.newInstance(majorName, minorName);
//        return build(plugin);
//    }

    public TPlugin build(TPlugin plugin) {
        String pluginName = getName();
        Helper.m.message(pluginName).expand("{").next();
        this.assignments.assignTo(plugin);
        String pid = PluginId.generateId(pluginName);
        if (plugin.id() == null) {
            plugin.setId(pid);
            Helper.m.collapse("} //id: " + pid).next();
        } else {
            Helper.m.collapse("}").next();
        }
        init(plugin);
        return plugin;
    }

    private void init(Object o) {
        String name = minorName == null ? majorName : String.format("%s.%s", majorName, minorName);
        try {
            PluginTools.initialize(o);
        } catch (IllegalArgumentException e) {
            throw new TConfigException("init plugin " + name + " failed, cause by " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            // ignore
        } catch (IllegalAccessException e) {
            System.err.println("warning: cannot access construct method of " + o.getClass());
        } catch (InvocationTargetException e) {
            Throwable source = e.getTargetException();
            throw new TConfigException("init plugin " + name + " failed, cause by " + source.getMessage(), source);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        String pluginName = getName();

        Plugin other = (Plugin) o;
        if (!pluginName.equals(other.getName())) {
            return false;
        }
        return assignments != null ? assignments.equals(other.assignments) : other.assignments == null;
    }

    @Override
    public int hashCode() {
        String pluginName = getName();
        int    result     = pluginName != null ? pluginName.hashCode() : 0;
        result = 31 * result + (assignments != null ? assignments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Plugin {" +
                "\nname='" + getName() + '\'' +
                "\nassignments=" + assignments +
                '}';
    }
}
