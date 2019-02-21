package com.aliyun.tauris.plugins.input.tailer;

import java.io.File;

/**
 * Created by ZhangLei on 17/8/26.
 */
public interface Tailer extends Runnable {

    File getFile();


    void stop();
}
