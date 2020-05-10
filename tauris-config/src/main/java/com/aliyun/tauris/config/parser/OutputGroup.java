package com.aliyun.tauris.config.parser;

import java.util.List;

/**
 * @author Ray Chaung
 */
public class OutputGroup extends PluginGroup {

    public OutputGroup(List<Plugin> plugins, Assignments assignments) {
        super("output", plugins, assignments);
    }
}
