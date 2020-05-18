package com.aliyun.tauris.plugins.input.tailer;

import java.io.File;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public interface TailerListener {

    /**
     * The tailer will call this method during construction,
     * giving the listener a method of stopping the tailer.
     * @param tailer the tailer.
     */
    void init(Tailer tailer);

    /**
     * This method is called if the tailed file is not found.
     * <p>
     * <b>Note:</b> this is called from the tailer thread.
     */
    void fileNotFound();


    void fileOpened(File file);
    /**
     * Called if a file rotation is detected.
     *
     * This method is called before the file is reopened, and fileNotFound may
     * be called if the new file has not yet been @author Ray Chaung<rockis@gmail.com>
     * <p>
     * <b>Note:</b> this is called from the tailer thread.
     */
    void fileRotated(File file);

    void fileClosed(File file);

    /**
     * Handles a line from a Tailer.
     * <p>
     * <b>Note:</b> this is called from the tailer thread.
     * @param line the line.
     */
    void handle(String line, long pos);

    /**
     * Handles an Exception .
     * <p>
     * <b>Note:</b> this is called from the tailer thread.
     * @param ex the exception.
     */
    void handle(File file, Exception ex);
}
