package com.aliyun.tauris.config.parser;

import java.util.List;

/**
 * @author Ray Chaung
 */
public class FilterGroup extends PluginGroup {

    public FilterGroup(List<Plugin> plugins, Assignments assignments) {
        super("filter", plugins, assignments);
    }
}
