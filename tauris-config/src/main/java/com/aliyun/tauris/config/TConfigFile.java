package com.aliyun.tauris.config;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ZhangLei on 17/5/14.
 */
public class TConfigFile implements TConfig {

    private File file;

    private String content;

    public TConfigFile(File file) {
        this.file = file;
    }

    @Override
    public String load() throws TConfigException {
        if (!file.exists()) {
            throw new TConfigException("No config file found: " + file);
        } else {
            try {
                content = FileUtils.readFileToString(file);
                return content;
            } catch (IOException e) {
                throw new TConfigException("Cannot read config file " + file, e);
            }
        }
    }
}
