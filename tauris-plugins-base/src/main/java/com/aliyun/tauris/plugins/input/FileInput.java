package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TScanner;
import com.aliyun.tauris.utils.TLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;

/**
 * Created by ZhangLei on 16/12/9.
 */
public class FileInput extends BaseStreamInput {

    private TLogger logger;

    @Required
    File filepath;

    boolean exitOnFinish;

    @Override
    protected void doInit() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        if (!filepath.exists()) {
            throw new TPluginInitException(String.format("file \"%s\" not exists", filepath.getAbsoluteFile()));
        }
    }

    @Override
    public void run() throws Exception {
        TScanner scanner = this.scanner.wrap(new BufferedInputStream(new FileInputStream(filepath))).withCodec(codec);
        scanner.scan((event) -> {
            try {
                putEvent(event);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        });
        scanner.close();
        if (exitOnFinish) {
            System.exit(0);
        }
    }

    @Override
    public void close() {
        super.close();
    }

}
