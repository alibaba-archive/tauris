package com.aliyun.tauris.plugins.output.file;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

import java.io.File;
import java.io.IOException;

/**
 * Class SimpleFileManager
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
@Name("simple")
public class SimpleFileManager extends AbstractFileManager {

    @Required
    File filepath;

    public SimpleFileManager() {
    }

    @Override
    public File resolve(TEvent event) throws IOException {
        return filepath;
    }

    @Override
    public boolean canClose(File file) {
        return false;
    }

    @Override
    public void onClose(File file) {
        super.onClose(file);
    }
}
