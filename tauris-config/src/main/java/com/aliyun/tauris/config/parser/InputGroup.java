package com.aliyun.tauris.config.parser;

import java.util.List;

/**
 * Created by chuanshi on 30.03.16.
 */
public class InputGroup extends PluginGroup {

    public InputGroup(List<Plugin> plugins, Assignments assignments) {
        super("input", plugins, assignments);
    }
}
