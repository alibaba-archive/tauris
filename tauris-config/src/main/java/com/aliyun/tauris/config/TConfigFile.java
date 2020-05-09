package com.aliyun.tauris.config;

import com.google.common.base.Charsets;

import java.io.*;

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
                byte[] content = new byte[(int)file.length()];
                InputStream is = new FileInputStream(file);
                is.read(content);
                return new String(content, Charsets.UTF_8);
            } catch (IOException e) {
                throw new TConfigException("Cannot read config file " + file, e);
            }
        }
    }
}
