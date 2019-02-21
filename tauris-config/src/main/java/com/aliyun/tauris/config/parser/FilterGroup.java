package com.aliyun.tauris.config.parser;

import java.util.List;

/**
 * Created by chuanshi on 30.03.16.
 */
public class FilterGroup extends PluginGroup {

    public FilterGroup(List<Plugin> plugins, Assignments assignments) {
        super("filter", plugins, assignments);
    }
}
