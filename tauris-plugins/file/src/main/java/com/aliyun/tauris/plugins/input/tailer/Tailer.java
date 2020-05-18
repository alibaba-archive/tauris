package com.aliyun.tauris.plugins.input.tailer;

import java.io.File;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public interface Tailer extends Runnable {

    File getFile();


    void stop();
}
