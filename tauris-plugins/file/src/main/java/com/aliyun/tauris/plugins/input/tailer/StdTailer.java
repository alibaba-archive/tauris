package com.aliyun.tauris.plugins.input.tailer;


import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;


/**
 * Simple implementation of the unix "tail -f" functionality.
 *
 * <h2>1. Create a TailerListener implementation</h2>
 * <p>
 * First you need to create a {@link TailerListener} implementation
 * ({@link TailerListenerAdapter} is provided for convenience so that you don't have to
 * implement every method).
 * </p>
 *
 * <p>For example:</p>
 * <pre>
 *  public class MyTailerListener extends TailerListenerAdapter {
 *      public void handle(String line) {
 *          System.out.println(line);
 *      }
 *  }</pre>
 *
 * <h2>2. Using a Tailer</h2>
 *
 * <p>
 * You can create and use a Tailer in one of three ways:
 * </p>
 * <ul>
 *   <li>Using one of the static helper methods:
 *     <ul>
 *       <li>{@link StdTailer#create(File, TailerListener)}</li>
 *       <li>{@link StdTailer#create(File, TailerListener, long)}</li>
 *       <li>{@link StdTailer#create(File, TailerListener, long, long)}</li>
 *     </ul>
 *   </li>
 *   <li>Using an {@link java.util.concurrent.Executor}</li>
 *   <li>Using an {@link Thread}</li>
 * </ul>
 *
 * <p>
 * An example of each of these is shown below.
 * </p>
 *
 * <h3>2.1 Using the static helper method</h3>
 *
 * <pre>
 *      TailerListener listener = new MyTailerListener();
 *      Tailer tailer = Tailer.create(file, listener, delay);</pre>
 *
 * <h3>2.2 Using an Executor</h3>
 *
 * <pre>
 *      TailerListener listener = new MyTailerListener();
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
 * <h3>2.3 Using a Thread</h3>
 * <pre>
 *      TailerListener listener = new MyTailerListener();
 *      Tailer tailer = new Tailer(file, listener, delay);
 *      Thread thread = new Thread(tailer);
 *      thread.setDaemon(true); // optional
 *      thread.start();</pre>
 *
 * <h2>3. Stopping a Tailer</h2>
 * <p>Remember to stop the tailer when you have done with it:</p>
 * <pre>
 *      tailer.stop();
 * </pre>
 *
 * <h2>4. Interrupting a Tailer</h2>
 * <p>You can interrupt the thread a tailer is running on by calling {@link Thread#interrupt()}.</p>
 * <pre>
 *      thread.interrupt();
 * </pre>
 * <p>If you interrupt a tailer, the tailer listener is called with the {@link InterruptedException}.</p>
 *
 * <p>The file is read using the default charset; this can be overridden if necessary</p>
 * @see TailerListener
 * @see TailerListenerAdapter
 * @version $Id$
 * @since 2.0
 * @since 2.5 Updated behavior and documentation for {@link Thread#interrupt()}
 */
public class StdTailer implements Runnable, Tailer {

    public static final int EOF = -1;

    private static final int DEFAULT_DELAY_MILLIS = 1000;

    private static final String RAF_MODE = "r";

    private static final int DEFAULT_BUFSIZE = 4096;

    private static final int POSITION_HEAD = 0;

    private static final int POSITION_TAIL = -1;

    // The default charset used for reading files
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    /**
     * Buffer on top of RandomAccessFile.
     */
    private final byte inbuf[];

    /**
     * The file which will be tailed.
     */
    private final File file;

    /**
     * The character set that will be used to read the file.
     */
    private final Charset charset;

    /**
     * The amount of time to wait for the file to be updated.
     */
    private final long delayMillis;

    /**
     * Whether to tail from the end or start of file
     * 0 : head
     * -1 : tail
     * other: position to position
     */
    private final long position;

    /**
     * The listener to notify of events when tailing.
     */
    private final TailerListener listener;

    /**
     * Whether to close and reopen the file whilst waiting for more input.
     */
    private final boolean reOpen;

    /**
     * The tailer will run as long as this value is true.
     */
    private volatile boolean run = true;

    /**
     * Creates a Tailer for the given file, starting from the beginning, with the default delay of 1.0s.
     * @param file The file to follow.
     * @param listener the TailerListener to use.
     */
    public StdTailer(final File file, final TailerListener listener) {
        this(file, listener, DEFAULT_DELAY_MILLIS);
    }

    /**
     * Creates a Tailer for the given file, starting from the beginning.
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     */
    public StdTailer(final File file, final TailerListener listener, final long delayMillis) {
        this(file, listener, delayMillis, POSITION_HEAD);
    }

    /**
     * Creates a Tailer for the given file, with a delay other than the default 1.0s.
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param position Seek to position.
     */
    public StdTailer(final File file, final TailerListener listener, final long delayMillis, final long position) {
        this(file, listener, delayMillis, position, DEFAULT_BUFSIZE);
    }

    /**
     * Creates a Tailer for the given file, with a delay other than the default 1.0s.
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param position Seek to position.
     * @param reOpen if true, close and reopen the file between reading chunks
     */
    public StdTailer(final File file, final TailerListener listener, final long delayMillis, final long position,
                     final boolean reOpen) {
        this(file, listener, delayMillis, position, reOpen, DEFAULT_BUFSIZE);
    }

    /**
     * Creates a Tailer for the given file, with a specified buffer size.
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param position Seek to position.
     * @param bufSize Buffer size
     */
    public StdTailer(final File file, final TailerListener listener, final long delayMillis, final long position,
                     final int bufSize) {
        this(file, listener, delayMillis, position, false, bufSize);
    }

    /**
     * Creates a Tailer for the given file, with a specified buffer size.
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param position Seek to position.
     * @param reOpen if true, close and reopen the file between reading chunks
     * @param bufSize Buffer size
     */
    public StdTailer(final File file, final TailerListener listener, final long delayMillis, final long position,
                     final boolean reOpen, final int bufSize) {
        this(file, DEFAULT_CHARSET, listener, delayMillis, position, reOpen, bufSize);
    }

    /**
     * Creates a Tailer for the given file, with a specified buffer size.
     * @param file the file to follow.
     * @param cset the Charset to be used for reading the file
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param position position to position
     * @param reOpen if true, close and reopen the file between reading chunks
     * @param bufSize Buffer size
     */
    public StdTailer(final File file, final Charset cset, final TailerListener listener, final long delayMillis,
                  final long position, final boolean reOpen
            , final int bufSize) {
        this.file = file;
        this.delayMillis = delayMillis;
        this.position = position;

        this.inbuf = new byte[bufSize];

        // Save and prepare the listener
        this.listener = listener;
        listener.init(this);
        this.reOpen = reOpen;
        this.charset = cset;
    }

    /**
     * Creates and starts a Tailer for the given file.
     *
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param seek position to position
     * @param bufSize buffer size.
     * @return The new tailer
     */
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis,
                                final long seek, final int bufSize) {
        return create(file, listener, delayMillis, seek, false, bufSize);
    }

    /**
     * Creates and starts a Tailer for the given file.
     *
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param seek position to position
     * @param reOpen whether to close/reopen the file between chunks
     * @param bufSize buffer size.
     * @return The new tailer
     */
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis,
                                final long seek, final boolean reOpen,
                                final int bufSize) {
        return create(file, DEFAULT_CHARSET, listener, delayMillis, seek, reOpen, bufSize);
    }

    /**
     * Creates and starts a Tailer for the given file.
     *
     * @param file the file to follow.
     * @param charset the character set to use for reading the file
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param seek position to position
     * @param reOpen whether to close/reopen the file between chunks
     * @param bufSize buffer size.
     * @return The new tailer
     */
    public static Tailer create(final File file, final Charset charset, final TailerListener listener,
                                final long delayMillis, final long seek, final boolean reOpen
            ,final int bufSize) {
        final Tailer tailer = new StdTailer(file, charset, listener, delayMillis, seek, reOpen, bufSize);
        final Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }

    /**
     * Creates and starts a Tailer for the given file with default buffer size.
     *
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param seek position to position
     * @return The new tailer
     */
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis,
                                final long seek) {
        return create(file, listener, delayMillis, seek, DEFAULT_BUFSIZE);
    }

    /**
     * Creates and starts a Tailer for the given file with default buffer size.
     *
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param seek position to position
     * @param reOpen whether to close/reopen the file between chunks
     * @return The new tailer
     */
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis,
                                final long seek, final boolean reOpen) {
        return create(file, listener, delayMillis, seek, reOpen, DEFAULT_BUFSIZE);
    }

    /**
     * Creates and starts a Tailer for the given file, starting at the beginning of the file
     *
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @return The new tailer
     */
    public static Tailer create(final File file, final TailerListener listener, final long delayMillis) {
        return create(file, listener, delayMillis, POSITION_HEAD);
    }

    /**
     * Creates and starts a Tailer for the given file, starting at the beginning of the file
     * with the default delay of 1.0s
     *
     * @param file the file to follow.
     * @param listener the TailerListener to use.
     * @return The new tailer
     */
    public static Tailer create(final File file, final TailerListener listener) {
        return create(file, listener, DEFAULT_DELAY_MILLIS, POSITION_HEAD);
    }

    /**
     * Return the file.
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets whether to keep on running.
     *
     * @return whether to keep on running.
     * @since 2.5
     */
    protected boolean isRun() {
        return run;
    }

    /**
     * Follows changes in the file, calling the TailerListener's handle method for each new line.
     */
    @Override
    public void run() {
        RandomAccessFile reader = null;
        try {
            long position = 0; // position within the file
            // Open the file
            while (isRun() && reader == null) {
                try {
                    reader = new RandomAccessFile(file, RAF_MODE);
                } catch (final FileNotFoundException e) {
                    listener.fileNotFound();
                }
                if (reader == null) {
                    Thread.sleep(delayMillis);
                } else {
                    // The current position in the file
                    if (this.position < 0 || this.position > file.length()) {
                        position = file.length();
                    } else if (this.position == POSITION_HEAD) {
                        position = 0;
                    } else {
                        position = this.position;
                    }
                    reader.seek(position);
                }
            }
            while (isRun()) {
                // Check the file length to see if it was rotated
                final long length = file.length();
                if (length < position) { //文件变小了
                    // File was rotated
                    listener.fileRotated(file);
                    // Reopen the reader after rotation ensuring that the old file is closed iff we re-open it
                    // successfully
                    try (RandomAccessFile save = reader) {
                        reader = new RandomAccessFile(file, RAF_MODE);
                        // At this point, we're sure that the old file is rotated
                        // Finish scanning the old file and then we'll start with the new one
                        try {
                            readLines(save);
                        }  catch (final IOException ioe) {
                            listener.handle(file, ioe);
                        }
                        position = 0;
                    } catch (final FileNotFoundException e) {
                        // in this case we continue to use the previous reader and position values
                        listener.fileNotFound();
                        Thread.sleep(delayMillis);
                    }
                    continue;
                } else {
                    // File was not rotated
                    // See if the file needs to be read again
                    if (length > position) { //文件变大，可以读了
                        // The file has more content than it did last time
                        position = readLines(reader);
                    }
                }
                if (reOpen && reader != null) {
                    reader.close();
                    listener.fileClosed(file);
                }
                Thread.sleep(delayMillis);
                if (isRun() && reOpen) {
                    reader = new RandomAccessFile(file, RAF_MODE);
                    reader.seek(position);
                }
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            listener.handle(file, e);
        } catch (final Exception e) {
            listener.handle(file, e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    listener.fileClosed(file);
                }
            }
            catch (final IOException e) {
                listener.handle(file, e);
            }
            stop();
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
     * @throws java.io.IOException if an I/O error occurs.
     */
    private long readLines(final RandomAccessFile reader) throws IOException {
        try (ByteArrayOutputStream lineBuf = new ByteArrayOutputStream(64)) {
            long pos = reader.getFilePointer();
            long rePos = pos; // position to re-read
            int num;
            boolean seenCR = false;
            while (isRun() && ((num = reader.read(inbuf)) != EOF)) {
                for (int i = 0; i < num; i++) {
                    final byte ch = inbuf[i];
                    switch ( ch ) {
                        case '\n':
                            seenCR = false; // swallow CR before LF
                            rePos = pos + i + 1;
                            listener.handle(new String(lineBuf.toByteArray(), charset), rePos);
                            lineBuf.reset();
                            break;
                        case '\r':
                            if (seenCR) {
                                lineBuf.write('\r');
                            }
                            seenCR = true;
                            break;
                        default:
                            if (seenCR) {
                                seenCR = false; // swallow final CR
                                rePos = pos + i + 1;
                                listener.handle(new String(lineBuf.toByteArray(), charset), rePos);
                                lineBuf.reset();
                            }
                            lineBuf.write(ch);
                    }
                }
                pos = reader.getFilePointer();
            }
            reader.seek(rePos); // Ensure we can re-read if necessary
            if (listener instanceof TailerListenerAdapter) {
                ((TailerListenerAdapter) listener).endOfFileReached();
            }
            return rePos;
        }
    }
}
