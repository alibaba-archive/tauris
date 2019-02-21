package com.aliyun.tauris.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple implementation of the unix "tail -f" functionality.
 * <p>
 * <h2>1. Create a ExTailerListener implementation</h3>
 * <p>
 * First you need to create a {@link ExTailerListener} implementation
 * ({@link ExTailerListenerAdapter} is provided for convenience so that you don't have to
 * implement every method).
 * </p>
 *
 * <p>For example:</p>
 * <pre>
 *  public class MyExTailerListener extends ExTailerListenerAdapter {
 *      public void handle(String line) {
 *          System.out.println(line);
 *      }
 *  }
 * </pre>
 *
 * <h2>2. Using a Tailer</h2>
 *
 * An example of each of these is shown below.
 *
 * <h3>2.1 Using the static helper method</h3>
 *
 * <pre>
 *      ExTailerListener listener = new MyExTailerListener();
 *      Tailer tailer = Tailer.create(file, listener, delay);
 * </pre>
 *
 * <h3>2.2 Use an Executor</h3>
 *
 * <pre>
 *      ExTailerListener listener = new MyExTailerListener();
 *      Tailer tailer = new Tailer(file, listener, delay);
 *
 *      // stupid executor impl. for demo purposes
 *      Executor executor = new Executor() {
 *          public void execute(Runnable command) {
 *              command.run();
 *           }
 *      };
 *
 *      executor.execute(tailer);
 * </pre>
 *
 *
 * <h3>2.3 Use a Thread</h3>
 * <pre>
 *      ExTailerListener listener = new MyExTailerListener();
 *      Tailer tailer = new Tailer(file, listener, delay);
 *      Thread thread = new Thread(tailer);
 *      thread.setDaemon(true); // optional
 *      thread.start();
 * </pre>
 *
 * <h2>3. Stop Tailing</h3>
 * <p>Remember to stop the tailer when you have done with it:</p>
 * <pre>
 *      tailer.stop();
 * </pre>
 *
 * @see ExTailerListener
 * @see ExTailerListenerAdapter
 * @version $Id: Tailer.java 1348698 2012-06-11 01:09:58Z ggregory $
 * @since 2.0
 */
public class ExTailer implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(ExTailer.class);

    private static final int DEFAULT_DELAY_MILLIS = 1000;

    private static final String RAF_MODE = "r";

    private static final int DEFAULT_BUFSIZE = 4096;

    /**
     * Buffer on top of RandomAccessFile.
     */
    private final byte inbuf[];

    /**
     * The file which will be tailed.
     */
    private final Path file;

    /**
     * The amount of time to wait for the file to be updated.
     */
    private final long delayMillis;

    /**
     * The listener to notify of events when tailing.
     */
    private final ExTailerListener listener;


    /**
     * The tailer will run as long as this value is true.
     */
    private volatile boolean run = true;

    /**
     * Creates a Tailer for the given file, starting from the beginning, with the default delay of 1.0s.
     * @param file The file to follow.
     * @param listener the ExTailerListener to use.
     */
    public ExTailer(Path file, ExTailerListener listener) {
        this(file, listener, DEFAULT_DELAY_MILLIS);
    }

    /**
     * Creates a Tailer for the given file, with a delay other than the default 1.0s.
     * @param file the file to follow.
     * @param listener the ExTailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     */
    public ExTailer(Path file, ExTailerListener listener, long delayMillis) {
        this(file, listener, delayMillis, DEFAULT_BUFSIZE);
    }

    /**
     * Creates a Tailer for the given file, with a specified buffer size.
     * @param fileLink the file to follow.
     * @param listener the ExTailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param bufSize Buffer size
     */
    public ExTailer(Path fileLink, ExTailerListener listener, long delayMillis, int bufSize) {
        if (!Files.isSymbolicLink(fileLink)) {
            throw new IllegalArgumentException(fileLink.toString() + " must be a symbolic link");
        }
        this.file = fileLink;
        this.delayMillis = delayMillis;

        this.inbuf = new byte[bufSize];

        // Save and prepare the listener
        this.listener = listener;
        listener.init(this);
    }

    /**
     * Return the file.
     *
     * @return the file
     */
    public Path getFile() {
        return file;
    }

    /**
     * Return the delay in milliseconds.
     *
     * @return the delay in milliseconds.
     */
    public long getDelay() {
        return delayMillis;
    }

    private File getRealFile() throws IOException {
        return Files.readSymbolicLink(file).toFile();
    }

    private boolean isLinkChanged(File realFile) throws IOException {
        return !getRealFile().equals(realFile);
    }

    /**
     * Follows changes in the file, calling the ExTailerListener's handle method for each new line.
     */
    public void run() {
        BufferedReader reader = null;
        try {
            File realFile = getRealFile();

            // Open the file
            while (run && reader == null) {
                try {
                    realFile = getRealFile();
                    reader = createReader(realFile, true);
                } catch (FileNotFoundException e) {
                    listener.fileNotFound();
                    sleep();
                    continue;
                }

                // The current position in the file
                reader.skip(realFile.length());
                LOG.info(String.format("open %s at %d", realFile.getName(), realFile.length()));
            }

            while (run) {
                long offset = readLines(reader);
                if (offset == 0 && isLinkChanged(realFile)) {
                    IOUtils.closeQuietly(reader);
                    listener.fileRotated();
                    realFile = getRealFile();
                    reader = createReader(realFile, false);
                } else {
                    sleep();
                }
            }
        } catch (Exception e) {
            LOG.error("Extailer has an exception", e);
            listener.handle(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private BufferedReader createReader(File file, boolean end) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        if (end) {
            fis.skip(file.length());
        }
        return new BufferedReader(new InputStreamReader(fis));
    }

    private void sleep() {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Allows the tailer to complete its current loop and return.
     */
    public void stop() {
        this.run = false;
    }

    /**
     * Read new lines.
     *
     * @param reader The file to read
     * @return The new position after the lines have been read
     * @throws IOException if an I/O error occurs.
     */
    private long readLines(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) return 0;
        long size = 0;

        do {
            listener.handle(line);
            size += line.getBytes().length;
            line = reader.readLine();
        } while (run && line != null);

        return size;
    }

}
