package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.metric.Counter;
import com.aliyun.tauris.utils.TLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Collections;

/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("tcp_nio")
public class TCPNIOInput extends BaseTInput {

    private static Counter RECEIVED = Counter.build().name("input_tcp_received_total").labelNames("id").help("Received bytes total").create().register();

    private TLogger logger;

    String host = "0.0.0.0";

    @Required
    int port;

    Charset charset = Charset.defaultCharset();

    int     backlog    = 128;
    boolean keepalive  = true;
    boolean reuseraddr = true;

    private EventLoopGroup bossGroup   = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void init() throws TPluginInitException {
        super.init();
        this.logger = TLogger.getLogger(this);
    }

    public void run() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline().addLast(new LineBasedFrameDecoder(8096));
                        ch.pipeline().addLast(new StringDecoder(charset));
                        ch.pipeline().addLast(new TcpInputHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, backlog)
                .option(ChannelOption.SO_REUSEADDR, reuseraddr)
                .childOption(ChannelOption.SO_KEEPALIVE, keepalive);

        ChannelFuture f = b.bind(InetAddress.getByName(host), port).sync();
        f.channel().closeFuture().sync();
    }

    @Override
    public void close() {
        super.close();
        logger.INFO("tcp input closing");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        logger.INFO("tcp input has been closed");
    }

    public class TcpInputHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            try {
                String text  = (String) msg;
                TEvent event = codec.decode(text);
                putEvent(event);
                RECEIVED.labels(id()).inc();
            } catch (DecodeException e) {
                logger.WARN2("decode error", e, e.getSource());
            } catch (Exception e) {
                logger.EXCEPTION(e);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }
    }
}
