package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.config.TConfigException;
import com.google.common.base.CaseFormat;

import java.util.List;
import java.util.Map;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class Assignments {

    private List<Assignment> assignments;

    public Assignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public void assignTo(Object object) {
        Map<String, PluginProperty> properties = PluginProperty.getProperties(object);
        Helper                      m          = Helper.m;
        for (Assignment a : assignments) {
            int curLineCount = m.getLineCount();
            String         name     = a.getName();
            PluginProperty property = properties.get(a.getName());
            if (property == null) {
                name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
                property = properties.get(name);
                if (name == null) {
                    throw new TConfigException(String.format("Unknown property '%s'", a.getName()));
                }
            }
            m.message(property.getName() + " => ");
            a.assignTo(property);
            m.trim();
            if (curLineCount == m.getLineCount()) {
                m.text(";");
            }
            properties.remove(a.getName());
            m.next();
        }
        properties.values().forEach((p) -> {
            if (p.isRequired()) {
                throw new TConfigException(String.format("%s missing a required property '%s'", object.getClass().getSimpleName(), p.getName()));
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Assignments component = (Assignments) o;

        return assignments != null ? assignments.equals(component.assignments) : component.assignments == null;

    }

    @Override
    public int hashCode() {
        return 31 * (assignments != null ? assignments.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "Method{" +
                "\nassignments=" + assignments +
                '}';
    }
}
