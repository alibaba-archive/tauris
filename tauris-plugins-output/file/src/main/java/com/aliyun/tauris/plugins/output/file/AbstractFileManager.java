package com.aliyun.tauris.plugins.output.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Class FormatFileManager
 *
 * @author yundun-waf-dev
 * @date 2018-10-25
 */
public abstract class AbstractFileManager implements TFileManager {

    private static Logger logger = LoggerFactory.getLogger(AbstractFileManager.class);

    /**
     * 如果非空, 则在关闭文件后将文件名加入后缀
     */
    String suffix;

    @Override
    public void onClose(File file) {
        if (suffix != null) {
            int idx = 0;
            while (true) {
                String filename = file.getName() + (idx == 0 ? "" : "_" + idx) + suffix;
                File nfile = new File(file.getParentFile(), filename);
                if (!nfile.exists()) {
                    if (!file.renameTo(nfile)) {
                        logger.error(String.format("rename %s to %s fail", file.getName(), file.getName()));
                    }
                    break;
                } else {
                    idx += 1;
                }
            }
        }
    }
}
