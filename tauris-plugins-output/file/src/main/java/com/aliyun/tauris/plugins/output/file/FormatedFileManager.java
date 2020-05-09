package com.aliyun.tauris.plugins.output.file;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.formatter.EventFormatter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Class FormatedFileManager
 *
 * @author yundun-waf-dev
 * @date 2018-10-25
 */
@Name("formatted")
public class FormatedFileManager extends AbstractFileManager {

    EventFormatter path;

    /**
     * 若文件在n秒内未写入, 则关闭文件.
     */
    int idleCloseTime = 60;

    private Set<File> generated = new HashSet<>();

    public FormatedFileManager() {
    }

    public FormatedFileManager(EventFormatter format) {
        this.path = format;
    }

    @Override
    public File resolve(TEvent event) throws IOException {
        File filepath = new File(path.format(event));
        if (generated.contains(filepath)) {
            return filepath;
        } else {
            File dir = filepath.getParentFile();
            if (dir != null && !dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException("make directory " + dir.getAbsolutePath() + " failed");
                }
            }
            generated.add(filepath);
        }
        return filepath;
    }

    @Override
    public boolean canClose(File file) {
        return System.currentTimeMillis() - file.lastModified() > idleCloseTime * 1000;
    }

    @Override
    public void onClose(File file) {
        super.onClose(file);
        generated.remove(file);
    }
}
