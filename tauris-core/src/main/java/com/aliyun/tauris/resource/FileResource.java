package com.aliyun.tauris.resource;

import com.aliyun.tauris.TResourceURI;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by ZhangLei on 2018/4/13.
 */
public class FileResource extends AbstractScheduleUpdateResource {

    /**
     * 检查文件md5，保证在加载文件时文件完整
     */
    private boolean checkMD5;

    private String md5Suffix = "md5sum";

    private long lastModified;
    private long fileLength;

    @Override
    public void setURI(TResourceURI uri) {
        super.setURI(uri);
        this.md5Suffix = uri.getParam("__md5sum__");
        this.checkMD5 = md5Suffix != null;
    }

    @Override
    public byte[] fetch() throws Exception {
        File file = resourceFile();
        if (!file.exists()) {
            return null;
        }
        if (checkMD5) {
            File md5file = new File(file.getAbsolutePath() + "." + md5Suffix);
            if (!md5file.exists()) {
                throw new IllegalStateException(String.format("md5sum file %s not exists", md5file.getAbsolutePath()));
            }
            String md5 = FileUtils.readFileToString(md5file, charset);
            byte[] content = FileUtils.readFileToByteArray(file);
            String newmd5 = md5sum(content);
            if (md5.trim().equals(newmd5)) {
                return content;
            }
            throw new RuntimeException("file " + file.getAbsoluteFile() + " md5sum mismatch");
        }

        return FileUtils.readFileToByteArray(file);
    }

    @Override
    protected byte[] fetchIfChanged() throws Exception {
        File file = resourceFile();
        if (!file.exists()) {
            return null;
        }
        if (file.length() == fileLength && file.lastModified() == lastModified) {
            return null;
        }
        byte[] bs = fetch();
        if (bs.length > 0) {
            lastModified = file.lastModified();
            fileLength = file.length();
        }
        return bs;
    }

    private File resourceFile() {
        String filepath = uri.toString().replaceFirst("file://", "");
        int    p        = filepath.indexOf('?');
        if (p > 0) {
            filepath = filepath.substring(0, p);
        }
        return new File(filepath);
    }

    @Override
    public String toString() {
        String filepath = uri.toString().replaceFirst("file://", "");
        int p = filepath.indexOf('?');
        if (p > 0) {
            filepath = filepath.substring(0, p);
        }
        return filepath;
    }
}
