package com.aliyun.tauris.plugins.scroll;

import com.aliyun.tauris.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

/**
 * Created by ZhangLei on 16/10/19.
 */
public class ScrollReceiver implements Runnable { // (1)

    private static Logger logger = LoggerFactory.getLogger(ScrollReceiver.class);

    private Socket socket;

    private TPipe<TEvent> pipe;

    private TScanner      scanner;
    private TDecoder      decoder;
    private TEventFactory eventFactory;

    private Consumer<String> onClose = null;

    /**
     * 从socket中读取字节流, 解析之后将日志内容输出到out中
     *
     * @param socket 与客户端连接的socket
     * @param pipe   输出管道
     */
    public ScrollReceiver(Socket socket,
                          TPipe<TEvent> pipe,
                          TScanner scanner,
                          TDecoder decoder,
                          TEventFactory eventFactory,
                          Consumer<String> onClose) {
        this.socket = socket;
        this.pipe = pipe;
        this.scanner = scanner;
        this.decoder = decoder;
        this.eventFactory = eventFactory;
        this.onClose = onClose;
    }

    @Override
    public void run() {
        String       remoteHost    = socket.getInetAddress().getHostAddress();
        String       remoteAddr    = String.format("%s:%s", socket.getInetAddress().getCanonicalHostName(), socket.getPort());
        InputStream  in            = null;
        long         receivedBytes = 0;
        ScrollReader reader        = new ScrollReader(pipe, scanner, decoder, eventFactory);
        try {
            in = socket.getInputStream();
            while (true) {
                try {
                    if (reader.read(in) < 0) {
                        logger.debug(remoteAddr + " client received {} bytes", receivedBytes);
                        break;
                    }
                } catch (EOFException | SocketException e) {
                    logger.info(remoteAddr + " stream closed, cause by " + e.getMessage());
                    break;
                } catch (Exception e) {
                    logger.warn(remoteAddr + " read error, close connection!", e);
                    break;
                }
            }
        } catch (IOException e) {
            logger.warn(remoteAddr + " stream was broken");
        } finally {
            logger.info(remoteAddr + " client disconnect");
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(socket);
            onClose.accept(remoteHost);
        }
    }

}