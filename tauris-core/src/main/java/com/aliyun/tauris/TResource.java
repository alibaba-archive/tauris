package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Type;
import com.google.common.base.CaseFormat;

import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * Created by ZhangLei on 2018/4/13.
 */
@Type("resource")
public abstract class TResource implements TPlugin {

    public static final String P_MD5SUM = "__md5sum__";

    protected TResourceURI uri;

    protected Charset charset = Charset.defaultCharset();

    public abstract byte[] fetch() throws Exception;

    public abstract void watch(Consumer<byte[]> consumer);

    public void setURI(TResourceURI uri) {
        this.uri = uri;
    }

    public TResourceURI getURI() {
        return uri;
    }

    public static TResource valueof(String uri) {
        TResourceURI u = TResourceURI.valueOf(uri);
        for (Class<? extends TResource> c : TPluginResolver.resolver().resolveSubTypes(TResource.class)) {
            if (schemeName(c).equals(u.getScheme())) {
                try {
                    TResource r = c.newInstance();
                    r.setURI(u);
                    return r;
                } catch (Exception e) {
                    throw new IllegalArgumentException("cannnot create resource instance", e);
                }
            }
        }
        throw new IllegalArgumentException("invalid resource:" + uri);
    }

    private static String schemeName(Class<? extends TResource> clazz) {
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
