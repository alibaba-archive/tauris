package com.aliyun.tauris.plugins.output.file;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;

import java.io.File;
import java.io.IOException;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type
public interface TFileManager extends TPlugin {

    File resolve(TEvent event) throws IOException;

    void onClose(File file);

    boolean canClose(File file);
}
