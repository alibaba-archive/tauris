package com.aliyun.tauris.plugins.scroll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;

import static com.aliyun.tauris.plugins.scroll.Scroll.*;

/**
 * Class ScrollFrameDecoder
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class ScrollFrameDecoder extends ByteToMessageDecoder {


    public ScrollFrameDecoder() {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < MIN_HEADER_LEN) {
            return;
        }
        int offset = in.readerIndex();

        byte[] magicBytes = new byte[2];
        in.getBytes(offset, magicBytes);

        if (magicBytes[0] != MAGIC_NUMBER[0] || magicBytes[1] != MAGIC_NUMBER[1]) {
            throw new CorruptedFrameException(String.format("invalid magic number:[%x%x]", magicBytes[0], magicBytes[1]));
        }

        int headerLen = in.getUnsignedShort(offset + MAGIC_PADDING_BYTES);
        if (headerLen > MAX_HEADER_LEN) {
            throw new TooLongFrameException(
                    "Scroll header length exceeds " + MAX_HEADER_LEN +
                            ": " + headerLen + " - discarded");
        }
        if (in.readableBytes() < MIN_HEADER_LEN + headerLen + BODY_LEN_BYTES) {
            return;
        }
        int bodyLen = in.getInt(offset + MIN_HEADER_LEN + headerLen);
        if (bodyLen > MAX_BODY_LEN) {
            throw new TooLongFrameException(
                    "Scroll body length exceeds " + MAX_BODY_LEN +
                            ": " + bodyLen + " - discarded");
        }
        int msgLen = MIN_HEADER_LEN + headerLen + BODY_LEN_BYTES + bodyLen;
        if (in.readableBytes() < msgLen) {
            return;
        }
        in.skipBytes(MIN_HEADER_LEN); // 2 bytes magic , 2 byte padding, 2 byte header length
        byte[] headerBytes = new byte[headerLen];
        in = in.readBytes(headerBytes);
        ScrollHeader header = decodeHeader(headerBytes);
        in.skipBytes(BODY_LEN_BYTES); //4bytes body length
        byte[] bodyBytes = new byte[bodyLen];
        in.readBytes(bodyBytes);
        decodeBody(bodyBytes, header, out);
    }

    private ScrollHeader decodeHeader(byte[] headerBytes) throws Exception {
        BiFunction<byte[], Integer, Integer> nextGap = (bytes, offset) -> {
            for (int i = offset; i < bytes.length; i++) {
                byte b = bytes[i];
                if (b == SCROLL_GAP) {
                    return i - offset;
                }
            }
            return bytes.length - offset;
        };
        ScrollHeader header = new ScrollHeader();

        byte type  = 0;
        int  nxGap = 0;
        for (int i = 0; i < headerBytes.length; i++) {
            type = headerBytes[i];
            nxGap = nextGap.apply(headerBytes, i);
            String head = new String(headerBytes, i, nxGap);
            switch (type) {
                case SCROLL_VERSION: //16
                    header.setVersion(head);
                    break;
                case SCROLL_APPNAME: //18
                    header.setAppName(head);
                    break;
                case SCROLL_TOKEN:   //20
                    header.setToken(head);
                    break;
                case SCROLL_HOSTNAME: //17
                    header.setHostname(head);
                    break;
            }
        }
        return header;
    }

    private void decodeBody(byte[] bodyBytes, ScrollHeader header, List<Object> out) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(bodyBytes));
        scanner.useDelimiter("\n");
        while (scanner.hasNext()) {
            byte[] bs = scanner.nextLine().getBytes();
            if (bs.length > 0) {
                out.add(new ScrollMessage(header, bs, bs.length));
            }
        }
        scanner.close();
    }
}
