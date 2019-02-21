package com.aliyun.tauris;

import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.utils.TConverter;
import com.google.common.base.CaseFormat;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangLei on 2018/5/9.
 */
public class PluginDoc {

    public static List<TPlugin> resolvePlugins(String typeName) throws ClassNotFoundException {

        TPluginResolver resolver = new TPluginResolver();

        TPluginFactory pluginFactory = resolver.resolvePluginFactory(typeName);
        List<TPlugin>  plugins       = new ArrayList<>();

        for (Class<? extends TPlugin> clazz : pluginFactory.getPluginClasses()) {
            try {
                TPlugin plugin = clazz.newInstance();
                plugins.add(plugin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return plugins;
    }

    public static List<FieldDoc> describePlugin(TPlugin plugin) {
        List<FieldDoc> docs   = new ArrayList<>();
        List<Field>    fields = new ArrayList<>();
        resolvePluginFields(plugin.getClass(), fields);
        for (Field f : fields) {
            if (f.getAnnotation(Deprecated.class) != null) {
                continue;
            }
            String name = f.getName();
            if (name.charAt(0) == '_' || Modifier.isPrivate(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            String sname = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
            FieldDoc doc = describeField(sname, plugin, f);
            if (doc != null) {
                docs.add(doc);
            }
        }
        return docs;
    }

    public static void resolvePluginFields(Class<? extends TPlugin> pluginClass, List<Field> fields) {
        if (pluginClass.isAssignableFrom(TPlugin.class)) {
            return;
        }
        if (pluginClass.getSuperclass() != null) {
            resolvePluginFields((Class<? extends TPlugin>) pluginClass.getSuperclass(), fields);
        }
        for (Field f : pluginClass.getDeclaredFields()) {
            String name = f.getName();
            if (name.charAt(0) == '_' || Modifier.isPrivate(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            fields.add(f);
        }
    }

    public static FieldDoc describeField(String name, TPlugin plugin, Field field) {
        field.setAccessible(true);
        Object value;
        try {
            value = field.get((plugin));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Class<?> fieldType = field.getType();
        boolean  isArray   = fieldType.isArray();
        boolean  required  = isRequired(field);
        if (isArray) {
            return new ArrayFieldDoc(name, fieldType, required);
        }
        if (String.class.isAssignableFrom(fieldType)) {
            return new SimpleFieldDoc(name, String.class, required, value, 2, "%s");
        }
        if (Character.class.isAssignableFrom(fieldType)) {
            return new SimpleFieldDoc(name, Character.class, required, value, 1, "%s");
        }
        if (Integer.class.isAssignableFrom(fieldType)
                || Long.class.isAssignableFrom(fieldType)
                || Short.class.isAssignableFrom(fieldType)
                || Byte.class.isAssignableFrom(fieldType)
                || value instanceof Integer
                || value instanceof Long
                || value instanceof Short
                || value instanceof Byte
                ) {
            return new SimpleFieldDoc(name, Integer.class, required, value, 0, "%d");
        }
        if (Float.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)) {
            return new SimpleFieldDoc(name, Float.class, required, value, 0, "%.1f");
        }
        if (value instanceof Boolean) {
            return new SimpleFieldDoc(name, Boolean.class, required, value, 0, "%s");
        }
        if (Map.class.isAssignableFrom(fieldType)) {
            return new MapFieldDoc(name, Map.class, required);
        }
        if (Enum.class.isAssignableFrom(fieldType)) {
            return new EnumFieldDoc(name, fieldType, required, value);
        }
        if (isPluginType(fieldType)) {
            return new PluginFieldDoc(name, fieldType, required);
        }
        if ((value == null || value instanceof String) && ConvertUtils.lookup(fieldType) != null) {
            return new ComplexFieldDoc(name, fieldType, required);
        }
        throw new IllegalArgumentException("unknown field type " + fieldType.getSimpleName());
    }

    public static String formatDescribe(String name, String f, Object desc, int q, String type, boolean required) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" => ");
        String s = "";
        if (q == 1) {
            s = desc == null ? "null" : String.format("'" + f + "'", desc);
        }
        if (q == 2) {
            s = desc == null ? "null" : String.format("\"" + f + "\"", desc);
        }
        if (q == 0) {
            s = desc == null ? "null" : String.format("" + f + "", desc);
        }
        sb.append(s).append(";");
        sb = new StringBuilder(StringUtils.rightPad(sb.toString(), 40));
        sb.append("#").append(type);
        if (required) {
            sb.append(", required");
        }
        return sb.toString();
    }

    public static boolean isRequired(Field field) {
        return field.getAnnotation(Required.class) != null;
    }

    public static boolean isPluginType(Class<?> clazz) {
        return TPlugin.class.isAssignableFrom(clazz);
    }

    public static boolean isWidgetType(Class<?> clazz) {
        return TWidget.class.isAssignableFrom(clazz);
    }

    abstract static class FieldDoc {
        protected String   name;
        protected Class<?> type;
        protected boolean  required;
        protected Object   value;

        public FieldDoc(String name, Class<?> type, boolean required, Object value) {
            this.name = name;
            this.type = type;
            this.required = required;
            this.value = value;
        }

        public String getTypeName() {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, type.getSimpleName());
        }
    }

    private static class SimpleFieldDoc extends FieldDoc {

        private int    quote;
        private String format;

        public SimpleFieldDoc(String name, Class clazz, boolean required, Object value, int quote, String format) {
            super(name, clazz, required, value);
            this.quote = quote;
            this.format = format;
        }

        public String toString() {
            return formatDescribe(name, format, value, quote, getTypeName(), required);
        }
    }

    private static class EnumFieldDoc extends FieldDoc {

        private Class<? extends Enum> enumClass;

        public EnumFieldDoc(String name, Class clazz, boolean required, Object value) {
            super(name, Enum.class, required, value);
            this.enumClass = clazz;
        }

        public String toString() {
            List<String> options       = new ArrayList<>();
            Enum[]       enumConstants = (Enum[]) enumClass.getEnumConstants();
            for (Enum enumConstant : enumConstants) {
                options.add(enumConstant.name());
            }
            return formatDescribe(name, "%s", value == null ? "null" : value.toString(), 2, "enum[" + String.join(", ", options) + "]", required);
        }
    }

    private static class MapFieldDoc extends FieldDoc {

        public MapFieldDoc(String name, Class clazz, boolean required) {
            super(name, clazz, required, "{}");
        }

        public String toString() {
            return formatDescribe(name, "%s", "{}", 0, "map", required);
        }
    }

    private static class ArrayFieldDoc extends FieldDoc {

        public ArrayFieldDoc(String name, Class clazz, boolean required) {
            super(name, clazz, required, null);
        }

        public String toString() {
            Class elemType = type.getComponentType();
            if (isPluginType(elemType)) {
                String plugin = String.format("plugin[]:%s", TPluginResolver.typeName(elemType));
                return formatDescribe(name, "%s", "{}", 0, plugin, required);
            } else {
                return formatDescribe(name, "%s", "[]", 0, elemType.getSimpleName().toLowerCase() + "[]", required);
            }
        }
    }

    private static class ComplexFieldDoc extends FieldDoc {

        public ComplexFieldDoc(String name, Class clazz, boolean required) {
            super(name, clazz, required, null);
        }

        public String toString() {
            Name na = type.getAnnotation(Name.class);
            String type = getTypeName();
            if (na != null) {
                type = na.value();
            }
            return formatDescribe(name, "%s", "null", 0, type, required);
        }
    }

    private static class PluginFieldDoc extends FieldDoc {

        public PluginFieldDoc(String name, Class type, boolean required) {
            super(name, type, required, null);
        }

        public String toString() {
            Class pluginType = TPluginResolver.resolvePluginType((Class<? extends TPlugin>) type);
            if (pluginType == null) {
                throw new IllegalArgumentException("unknown plugin type of class " + type);
            }
            String typeName = TPluginResolver.typeName(pluginType);
            String n        = pluginType.equals(type) ? typeName : typeName + "." + TPluginResolver.pluginName((Class<? extends TPlugin>) type);
            return formatDescribe(name, "%s", "null", 0, String.format("plugin:%s", n), required);
        }
    }

    private static boolean isDeprecated(Class<?> clazz) {
        return clazz.getAnnotation(Deprecated.class) != null;
    }

    public static void printDoc(String typeName, String pluginName) throws Exception {
        TConverter.register();
        List<TPlugin> plugins = PluginDoc.resolvePlugins(typeName);
        if (pluginName.isEmpty()) {
            System.out.println(" ------------ " + typeName + " ------------ ");
        }
        for (TPlugin plugin : plugins) {
            if (isDeprecated(plugin.getClass())) {
                continue;
            }
            String name = TPluginResolver.pluginName((plugin.getClass()));
            if (name.equals(pluginName)) {
                System.out.println(pluginName + " {");
                for (PluginDoc.FieldDoc doc : PluginDoc.describePlugin(plugin)) {
                    System.out.print("    " + doc + "\n");
                }
                System.out.println("}");
            }
            if (pluginName.isEmpty()) {
                System.out.println("  * " + name);
            }
        }
    }
}
