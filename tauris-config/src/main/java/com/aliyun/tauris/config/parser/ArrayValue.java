package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.config.TConfigException;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class ArrayValue extends Value {

    private List<SimpleValue> elements;

    public ArrayValue(List<SimpleValue> elements) {
        this.elements = elements;
    }

    @Override
    void _assignTo(PluginProperty property) throws Exception {
        if (property.isArray()) {
            assignToArray(property);
            return;
        }
        if (property.getType().isAssignableFrom(List.class)) {
            assignToCollection(property, new ArrayList<>());
            return;
        }
        if (property.getType().isAssignableFrom(Set.class)) {
            assignToCollection(property, new HashSet<>());
            return;
        }
        throw new TConfigException(String.format("cannot assign an array to %s", property.getType()));
    }

    void assignToArray(PluginProperty property) throws Exception {
        Object array = Array.newInstance(property.getType(), elements.size());
        int i = 0;
        for (SimpleValue e : elements) {
            Object ev = ConvertUtils.convert(e.value(), property.getType());
            Array.set(array, i, ev);
            i++;
        }
        property.set(array);
    }

    void assignToCollection(PluginProperty property, Collection<Object> collection) throws Exception {
        for (SimpleValue e : elements) {
            Object ev = e.value();
            if (property.isArray()) {
                ev = ConvertUtils.convert(e.value(), property.getType());
            } else if (property.isCollection() && property.getValueType() != null) {
                ev = ConvertUtils.convert(e.value(), property.getValueType());
            }
            collection.add(ev);
        }
        property.set(collection);
    }

    @Override
    public String toString() {
        List<String> es = new ArrayList<>(elements.size());
        for (SimpleValue v: elements) {
            es.add(v.toString());
        }
        return "[\n\t" + String.join(",\n\t", es) + "]";
    }
}
