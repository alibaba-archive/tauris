package com.aliyun.tauris.utils;

import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.annotations.ValueType;
import com.google.common.base.CaseFormat;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by ZhangLei on 16/12/10.
 */
public abstract class TProperty implements Comparable<TProperty> {

    protected final Object object;
    protected final String name;
    protected final Class  type;
    protected final Class  valueType;

    static {
        TConverter.register();
    }

    public static Map<String, TProperty> getProperties(Object object) {
        if (object == null) throw new IllegalArgumentException("object cannot be null.");
        Class<?> type = object.getClass();
        Class[] noArgs = new Class[0], oneArg = new Class[1];
        Map<String, TProperty> properties = new HashMap<>();
        for (Field field : getAllFields(type)) {
            if (field.getName().startsWith("_")) continue;
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) continue;

            String underScoreName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            String upperCamelName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, field.getName());

            Method getMethod = null, setMethod = null;
            try {
                oneArg[0] = field.getType();
                setMethod = type.getMethod("set" + upperCamelName, oneArg);
            } catch (Exception ignored) {
            }
            try {
                getMethod = type.getMethod("get" + upperCamelName, noArgs);
            } catch (Exception ignored) {
            }
            if (getMethod != null && (setMethod != null)) {
                properties.put(underScoreName, new MethodProperty(object, underScoreName, setMethod, getMethod));
                continue;
            }

            if (!Modifier.isPublic(modifiers)) {
                field.setAccessible(true);
            }
            properties.put(underScoreName, new FieldProperty(object, field));
        }
        Class nextClass = type;
        while (nextClass != Object.class) {
            for (Method m : nextClass.getMethods()) {
                String methodName = m.getName();
                if (!methodName.startsWith("set")
                        || methodName.length() <= 3
                        || m.getParameterTypes().length != 1
                        || !Modifier.isPublic(m.getModifiers())
                        || !m.getReturnType().equals(Void.TYPE)) {
                    continue;
                }
                Class<?> propType = m.getParameterTypes()[0];
                String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, methodName.substring(3));
                Method getter = null;
                try {
                    Method g = nextClass.getMethod("get" + methodName.substring(3));
                    if (g.getReturnType().equals(propType)
                            && g.getParameterTypes().length == 0
                            && Modifier.isPublic(g.getModifiers())) {
                        getter = g;
                    }
                } catch (Exception e) {
                }
                properties.put(name, new MethodProperty(object, name, propType, m, getter));
            }
            nextClass = nextClass.getSuperclass();
        }
        return properties;
    }

    private static List<Field> getAllFields(Class type) {
        List<Field> allFields = new ArrayList<>();
        Class nextClass = type;
        while (nextClass != Object.class) {
            Collections.addAll(allFields, nextClass.getDeclaredFields());
            nextClass = nextClass.getSuperclass();
        }
        return allFields;
    }

    TProperty(Object object, String name, Class type, ValueType valueType) {
        this.object = object;
        this.name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        this.type = type;
        this.valueType = valueType == null ? null : valueType.value();
    }

    public boolean isArray() {
        return type.isArray();
    }

    public boolean isCollection() {
        return ClassUtils.isAssignable(type, Collection.class);
    }

    public boolean isMap() {
        return ClassUtils.isAssignable(type, Map.class);
    }

    public Class<?> getType() {
        return type.isArray() ? type.getComponentType() : type;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public int compareTo(TProperty o) {
        int comparison = name.compareTo(o.name);
        if (comparison != 0) {
            // Sort id and name above all other fields.
            if (name.equals("id")) return -1;
            if (o.name.equals("id")) return 1;
            if (name.equals("name")) return -1;
            if (o.name.equals("name")) return 1;
        }
        return comparison;
    }

    abstract public void set(Object value) throws Exception;

    abstract public Object get() throws Exception;

    abstract public boolean isNull() throws Exception;

    abstract public boolean isRequired();

    public Class<?> getValueType() {
        return this.valueType;
    }

    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TProperty other = (TProperty) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (type == null) {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        return true;
    }

    static class MethodProperty extends TProperty {
        private final Method setMethod, getMethod;

        MethodProperty(Object object, String name, Class type, Method setMethod, Method getMethod) {
            super(object, name, type, getMethod == null ? null : getMethod.getAnnotation(ValueType.class));
            this.setMethod = setMethod;
            this.getMethod = getMethod;
        }

        MethodProperty(Object object, String name, Method setMethod, Method getMethod) {
            this(object, name, resolveType(setMethod, getMethod), setMethod, getMethod);
        }

        public static Class<?> resolveType(Method setMethod, Method getMethod) {
            if (setMethod == null && getMethod == null) {
                throw new IllegalArgumentException("setter and getter both null");
            }
            if (getMethod != null) {
                return getMethod.getReturnType();
            }
            return setMethod.getParameterTypes()[0];
        }

        public void set(Object value) throws Exception {
            if (setMethod == null) {
                throw new IllegalStateException(name + " is writeonly");
            }
            value = ConvertUtils.convert(value, getType());
            setMethod.invoke(object, value);
        }

        public Object get() throws Exception {
            if (getMethod == null) {
                throw new IllegalStateException(name + " is readonly");
            }
            return getMethod.invoke(object);
        }

        public boolean isReadonly() {
            return getMethod == null;
        }

        public boolean isWriteonly() {
            return setMethod == null;
        }

        @Override
        public boolean isNull() throws Exception {
            return getMethod.invoke(object) == null;
        }

        @Override
        public boolean isRequired() {
            if (getMethod != null) {
                return getMethod.getAnnotation(Required.class) != null;
            }
            return setMethod.getAnnotation(Required.class) != null;
        }

    }

    static class FieldProperty extends TProperty {
        private final Field field;

        protected FieldProperty(Object object, Field field) {
            super(object, field.getName(), field.getType(), field.getAnnotation(ValueType.class));
            this.field = field;
        }

        public void set(Object value) throws Exception {
            if (this.field.getType().isEnum()) {
                String enumValName = (String) value;
                Enum[] enumConstants = (Enum[]) this.field.getType().getEnumConstants();
                Enum eval = null;
                for (Enum enumConstant : enumConstants) {
                    if (enumConstant.name().equals(enumValName)) {
                        eval = enumConstant;
                    }
                }
                if (eval == null) {
                    throw new IllegalArgumentException(String.format("%s has not enum value named %s", this.field.getType(), value));
                } else {
                    field.set(object, eval);
                }
            } else {
                if (this.field.getType().isArray() && !value.getClass().isArray()) {
                    Class<?> compType = this.field.getType().getComponentType();
                    Object array = Array.newInstance(compType, 1);
                    Array.set(array, 0, ConvertUtils.convert(value, compType));
                    field.set(object, array);
                } else {
                    value = ConvertUtils.convert(value, this.field.getType());
                    field.set(object, value);
                }
            }
        }

        public Object get() throws Exception {
            return field.get(object);
        }

        @Override
        public boolean isNull() throws Exception {
            return field.get(object) == null;
        }

        @Override
        public boolean isRequired() {
            return field.getAnnotation(Required.class) != null;
        }
    }
}
