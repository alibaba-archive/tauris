package com.aliyun.tauris.config;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Type;
import com.aliyun.tauris.config.parser.PluginPropertyConverter;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class TPluginResolverInitializer
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class TPluginResolverInitializer implements TPluginScanner {

    private Reflections reflections;

    public static void initialize(ClassLoader parent, File ...pluginDirs) {
        TPluginClassLoader cl = new TPluginClassLoader(parent, pluginDirs);
        Thread.currentThread().setContextClassLoader(cl);
        TPluginResolverInitializer initializer = new TPluginResolverInitializer(cl);
        TPluginResolver.configure(initializer);
        PluginPropertyConverter.configure(initializer);
    }

    public TPluginResolverInitializer(ClassLoader classLoader) {
        Configuration cfg = ConfigurationBuilder.build(classLoader, TEvent.class.getPackage().getName());
        reflections = new Reflections(cfg);
    }

    @Override
    public Set<Class<? extends TPlugin>> scanPluginTypes() {
        Set<Class<? extends TPlugin>> types = new HashSet<>();
        for (Class<? extends TPlugin> s : reflections.getSubTypesOf(TPlugin.class)) {
            if (s.getAnnotation(Type.class) != null) {
                types.add(s);
            }
        }
        return types;
    }

    @Override
    public Set<Class<? extends TPlugin>> scanPluginClasses(Class<? extends TPlugin> pluginType) {
        Set<Class<? extends TPlugin>> types = new HashSet<>();
        for (Class<? extends TPlugin> s : reflections.getSubTypesOf(pluginType)) {
            int modifier = s.getModifiers();
            if (s.isInterface() || Modifier.isAbstract(modifier) || !Modifier.isPublic(modifier)) {
                continue;
            }
            types.add(s);
        }
        return types;
    }

    public static URL[] findJars(File pluginDir) {
        List<URL> urls = new ArrayList<>();
        try {
            Files.walk(pluginDir.toPath()).filter((p) -> p.getFileName().toFile().getName().endsWith(".jar")).forEach((p) -> {
                try {
                    urls.add(p.toUri().toURL());
                } catch (MalformedURLException e) {
                }
            });
            return urls.toArray(new URL[urls.size()]);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
