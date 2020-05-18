package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.PluginTools;
import com.aliyun.tauris.config.TConfigException;

import java.lang.reflect.InvocationTargetException;

/**
 * example:
 *   remove_if_value => {
 *       equals => "-";
 *   }
 * @author Ray Chaung<rockis@gmail.com>
 */
class AssignmentsValue extends Value {

    private final Assignments assignments;

    public AssignmentsValue(Assignments assignments) {
        this.assignments = assignments;
    }

    @Override
    void _assignTo(PluginProperty property) throws Exception {
        //此属性是一个复杂对象
        Helper.m.expand("{").next();
        Object propertyValue = property.getType().newInstance();
        assignments.assignTo(propertyValue);
        property.set(propertyValue);
        init(propertyValue, property.getName());
        Helper.m.collapse("}");
    }


    private void init(Object o, String name) {
        try {
            PluginTools.initialize(o);
        } catch (NoSuchMethodException e) {
            // ignore
        } catch (IllegalAccessException e) {
            System.err.println("warning: cannot access init method of " + o.getClass());
        } catch (InvocationTargetException e) {
            // throw
            e.printStackTrace();
            String message = e.getMessage();
            if (message == null) {
                message = e.getTargetException().getMessage();
            }
            throw new TConfigException("init component " + name + " failed, cause by " + message, e);
        }
    }

    @Override
    public String toString() {
        return "";
    }
}
