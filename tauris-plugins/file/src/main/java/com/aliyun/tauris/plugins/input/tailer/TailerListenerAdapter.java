package com.aliyun.tauris.plugins.input.tailer;

import java.io.File;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class TailerListenerAdapter implements TailerListener {

    /**
     * The tailer will call this method during construction,
     * giving the listener a method of stopping the tailer.
     * @param tailer the tailer.
     */
    public void init(Tailer tailer) {
    }

    /**
     * This method is called if the tailed file is not found.
     */
    public void fileNotFound() {
    }

    @Override
    public void fileOpened(File file) {

    }

    /**
     * Called if a file rotation is detected.
     *
     * This method is called before the file is reopened, and fileNotFound may
     * be called if the new file has not yet been @author Ray Chaung<rockis@gmail.com>
     */
    public void fileRotated(File file) {
    }

    @Override
    public void fileClosed(File file) {

    }

    /**
     * Handles a line from a Tailer.
     * @param line the line.
     */
    public void handle(String line, long pos) {
    }

    /**
     * Handles an Exception .
     * @param ex the exception.
     */
    public void handle(File file, Exception ex) {
    }

    /**
     * Called each time the Tailer reaches the end of the file.
     *
     * <b>Note:</b> this is called from the tailer thread.
     *
     * Note: a future version of commons-io will pull this method up to the TailerListener interface,
     * for now clients must subclass this class to use this feature.
     *
     * @since 2.5
     */
    public void endOfFileReached() {
    }
}

