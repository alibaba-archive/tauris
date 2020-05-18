package com.aliyun.tauris.plugins.scroll;

import com.aliyun.tauris.TEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.TooLongFrameException;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;

/**
 * Class ScrollFrameDecoder
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class ScrollFrameEncoder extends MessageToByteEncoder<ByteBuf> {

    String version = "1.0";
    String appName = "tauris";
    String token   = "";
    String hostname = "";

    public ScrollFrameEncoder(String version, String appName, String token, String hostname) {
        this.version = version;
        this.appName = appName;
        this.token = token;
        this.hostname = hostname;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        out.writeBytes(Scroll.MAGIC_NUMBER);
        out.writeByte(0x01);
        out.writeByte(0x01);

        ByteBuf header = Unpooled.buffer(48);
        header.writeByte(Scroll.SCROLL_TIMESTAMP);
        header.writeLong(System.currentTimeMillis());
        if (!hostname.isEmpty()) {
            header.writeByte(Scroll.SCROLL_GAP);
            header.writeByte(Scroll.SCROLL_HOSTNAME);
            header.writeCharSequence(hostname, Charset.defaultCharset());
        }
        if (!appName.isEmpty()) {
            header.writeByte(Scroll.SCROLL_GAP);
            header.writeByte(Scroll.SCROLL_APPNAME);
            header.writeCharSequence(appName, Charset.defaultCharset());
        }
        if (!token.isEmpty()) {
            header.writeByte(Scroll.SCROLL_GAP);
            header.writeByte(Scroll.SCROLL_TOKEN);
            header.writeCharSequence(token, Charset.defaultCharset());
        }
        if (!version.isEmpty()) {
            header.writeByte(Scroll.SCROLL_GAP);
            header.writeByte(Scroll.SCROLL_VERSION);
            header.writeCharSequence(version, Charset.defaultCharset());
        }
        out.writeShort(header.writerIndex());
        out.writeBytes(header);
        out.writeInt(in.writerIndex());
        out.writeBytes(in);
    }
}
