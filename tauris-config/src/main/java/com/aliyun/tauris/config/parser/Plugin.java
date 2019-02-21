package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.*;
import com.aliyun.tauris.config.TConfigException;
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Created by jdziworski on 30.03.16.
 */
public class Plugin {
    private String      name;
    private Assignments assignments;

    public Plugin(String name, Assignments assignments) {
        this.name = name;
        this.assignments = assignments;
    }

    public String getName() {
        return name;
    }

    public  <T extends TPlugin> TPlugin build(Class<T> type) {
        TPluginResolver resolver =  TPluginResolver.defaultResolver;
        TPlugin plugin = resolver.resolvePlugin(type, name);
        return this.build(plugin);
    }

    public TPlugin build(TPluginFactory factory) {
        TPlugin plugin = factory.newInstance(name);
        return build(plugin);
    }

    public TPlugin build(TPlugin plugin) {
        Helper.m.message(name).expand("{").next();
        this.assignments.assignTo(plugin);
        String pid = name + "_" + RandomStringUtils.randomNumeric(8).toLowerCase();
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
            Method initMethod = o.getClass().getMethod("init");
            initMethod.invoke(o);
        } catch (IllegalArgumentException e) {
            throw new TConfigException("init component " + name + " failed, cause by " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            // ignore
        } catch (IllegalAccessException e) {
            System.err.println("warning: cannot access init method of " + o.getClass());
        } catch (InvocationTargetException e) {
            // throw
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
