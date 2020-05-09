package com.aliyun.tauris.config.parser;

/**
 * Created by jdziworski on 30.03.16.
 */
public class Assignment {

    private final String name;

    private final Value value;

    public Assignment(String name, Value value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void assignTo(TProperty property) {
        this.value.assignTo(property);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Assignment that = (Assignment) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "assignment{" +
                "\nname='" + name + '\'' +
                '}';
    }
}
