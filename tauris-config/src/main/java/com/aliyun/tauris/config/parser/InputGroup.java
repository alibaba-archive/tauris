package com.aliyun.tauris.config.parser;

import java.util.List;

/**
 * @author Ray Chaung
 */
public class InputGroup extends PluginGroup {

    public InputGroup(List<Plugin> plugins, Assignments assignments) {
        super("input", plugins, assignments);
    }
}
