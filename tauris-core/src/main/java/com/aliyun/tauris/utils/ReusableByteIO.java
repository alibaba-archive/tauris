package com.aliyun.tauris.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ZhangLei on 16/10/20.
 */
public class ReusableByteIO implements ByteReader, BytesWriter{

    private static Logger LOG = LoggerFactory.getLogger(ReusableByteIO.class);

    private ArrayBlockingQueue<Element> free;
    private ArrayBlockingQueue<Element> used ;

    /**
     *
     */
    public ReusableByteIO(int capacity, int blockSize) {
        this.free = new ArrayBlockingQueue<>(capacity);
        this.used = new ArrayBlockingQueue<>(capacity);
        for (int i = 0; i < capacity; i++) {
            this.free.add(new Element(blockSize));
        }
    }

    public void write(byte[] buffer, int length) throws IOException {
        try {
            Element e = free.take();
            if (length > e.value.length) {
                throw new IllegalStateException("buffer length too large than " + e.length);
            }
            System.arraycopy(buffer, 0, e.value, 0, length);
            e.length = length;
            used.put(e);
        } catch (InterruptedException x) {
            throw new EOFException();
        }
    }

    @Override
    public void write(InputStream input, int length) throws IOException {
        try {
            Element e = free.take();
            if (length > e.value.length) {
                throw new IllegalStateException("buffer length too large than " + e.length);
            }
            e.length = IOUtils.read(input, e.value, 0, length);
            if (!used.offer(e)) {
                LOG.warn("queue full");
            }
        } catch (InterruptedException x) {
            throw new EOFException();
        }
    }

    /**
     */
    public int read(byte[] buffer) throws IOException {
        try {
            Element e = used.take();
            int length = e.length;
            if (length > buffer.length) {
                throw new IllegalStateException("buffer size is less than " + e.length);
            }
            System.arraycopy(e.value, 0, buffer, 0, length);
            e.reset();
            free.put(e);
            return length;
        } catch (InterruptedException x) {
            throw new EOFException();
        }
    }

    public void close() {
        free.clear();
        used.clear();
    }

    static class Element {
        private byte[] value;
        private int length;

        public Element(int blockSize) {
            this.value = new byte[blockSize];
        }

        public void reset() {
            this.length = 0;
        }
    }
}
