package com.aliyun.tauris.config.parser;

import java.util.List;

/**
 * Created by chuanshi on 30.03.16.
 */
public class OutputGroup extends PluginGroup {

    public OutputGroup(List<Plugin> plugins, Assignments assignments) {
        super("output", plugins, assignments);
    }
}
