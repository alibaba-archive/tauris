package com.aliyun.tauris.config;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;

/**
 * Class CombinedClassLoader
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class TPluginClassLoader extends ClassLoader {

    private ClassLoader[] delegateClassLoaders;


    public TPluginClassLoader(ClassLoader parent, File... pluginDirs) {
        this(parent, resolvePluginClassLoader(pluginDirs));
    }

    public TPluginClassLoader(ClassLoader parent, ClassLoader... delegateClassLoaders) {
        super(parent);
        this.delegateClassLoaders = delegateClassLoaders;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/').concat(".class");
        URL    url  = findResource(path);
        if (url == null) {
            throw new ClassNotFoundException(name);
        }
        ByteBuffer byteCode;
        try {
            byteCode = loadResource(url);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
        return defineClass(name, byteCode, null);
    }

    private ByteBuffer loadResource(URL url) throws IOException {
        InputStream stream = null;
        try {
            stream = url.openStream();
            int initialBufferCapacity = Math.min(0x40000, stream.available() + 1);
            if (initialBufferCapacity <= 2) {
                initialBufferCapacity = 0x10000;
            } else {
                initialBufferCapacity = Math.max(initialBufferCapacity, 0x200);
            }
            ByteBuffer buf = ByteBuffer.allocate(initialBufferCapacity);
            while (true) {
                if (!buf.hasRemaining()) {
                    ByteBuffer newBuf = ByteBuffer.allocate(2 * buf.capacity());
                    buf.flip();
                    newBuf.put(buf);
                    buf = newBuf;
                }
                int len = stream.read(buf.array(), buf.position(), buf.remaining());
                if (len <= 0) {
                    break;
                }
                buf.position(buf.position() + len);
            }
            buf.flip();
            return buf;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    protected URL findResource(String name) {
        for (ClassLoader delegate : delegateClassLoaders) {
            URL resource = delegate.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    protected Enumeration<URL> findResources(String name) throws IOException {
        Vector<URL> vector = new Vector<>();
        for (ClassLoader delegate : delegateClassLoaders) {
            Enumeration<URL> enumeration = delegate.getResources(name);
            while (enumeration.hasMoreElements()) {
                vector.add(enumeration.nextElement());
            }
        }
        return vector.elements();
    }

    public static URLClassLoader[] resolvePluginClassLoader(File ...pluginDirs) {
        Map<String, List<URL>> pluginUrls = new HashMap<>();
        for (File pluginDir: pluginDirs) {
            File[] pds = pluginDir.listFiles();
            if (pds == null) {
                continue;
            }
            for (File pd : pds) {
                String packageName = pd.getName();
                File[] spds = pluginDir.listFiles();
                if (spds == null) {
                    continue;
                }
                List<URL> urls = pluginUrls.get(packageName);
                if (urls == null) {
                    urls = new ArrayList<>();
                    pluginUrls.put(packageName, urls);
                }
                urls.addAll(findJars(pd));
            }
        }
        URLClassLoader[] us = new URLClassLoader[pluginUrls.size()];
        int i = 0;
        for (List<URL> urs : pluginUrls.values()) {
            us[i++] = new URLClassLoader(urs.toArray(new URL[urs.size()]));
        }
        return us;
    }

    public static List<URL> findJars(File pluginDir) {
        List<URL> urls = new ArrayList<>();
        try {
            Files.walk(pluginDir.toPath()).filter((p) -> p.getFileName().toFile().getName().endsWith(".jar")).forEach((p) -> {
                try {
                    urls.add(p.toUri().toURL());
                } catch (MalformedURLException e) {
                }
            });
            return urls;
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
