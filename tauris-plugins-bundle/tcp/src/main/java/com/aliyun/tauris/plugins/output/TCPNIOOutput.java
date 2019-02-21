package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.metric.Counter;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("tcp_nio")
public class TCPNIOOutput extends BaseTOutput {

    private static Counter SENT         = Counter.build().name("input_tcp_sent_total").labelNames("id").help("Sent tcp packets").create().register();
    private static Counter SENT_FAILED  = Counter.build().name("input_tcp_sent_failed_total").labelNames("id").help("Sent tcp failed packets").create().register();
    private static Counter RECONN_TIMES = Counter.build().name("input_tcp_reconnect_total").labelNames("id").help("Sent tcp reconnect times").create().register();

    private static Logger logger = LoggerFactory.getLogger(TCPNIOOutput.class);

    @Required
    String host;

    @Required
    int port;

    private Bootstrap      bootstrap   = new Bootstrap();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private volatile ChannelHandlerContext context;

    public void init() throws TPluginInitException {
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ClientHandler());
            }
        });
    }

    @Override
    public void start() throws Exception {
        bootstrap.connect(host, port).addListener(new ConnectionListener());
    }

    public void doWrite(TEvent event) {
        try {
            while (context == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            codec.encode(event, writer);
            byte[] message = writer.toString().getBytes();
            ByteBuf buf = Unpooled.buffer(message.length);
            buf.writeBytes(message);
            context.writeAndFlush(buf);
            SENT.labels(id()).inc();
        } catch (EncodeException e) {
            logger.warn("encode event failed", e);
        } catch (IOException e) {
            SENT_FAILED.labels(id()).inc();
            logger.warn("send tcp packet failed", e);
        }
    }

    @Override
    public void stop() {
        super.stop();
        workerGroup.shutdownGracefully();
    }

    private class ClientHandler extends SimpleChannelInboundHandler {

        public ClientHandler() {
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logger.info(String.format("%s channel %s actived", id(), ctx.channel().id().asLongText()));
            context = ctx;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logger.warn(String.format("%s channel %s inactived", id(), ctx.channel().id().asLongText()));
            context = null;
            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(() -> {
                RECONN_TIMES.labels(id()).inc();
                try {
                    start();
                } catch (Exception e) {
                }
            }, 1L, TimeUnit.SECONDS);
        }
    }

    public class ConnectionListener implements ChannelFutureListener {

        public ConnectionListener() {
        }

        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if (!channelFuture.isSuccess()) {
                final EventLoop loop = channelFuture.channel().eventLoop();
                loop.schedule(() -> {
                    RECONN_TIMES.labels(id()).inc();
                    try {
                        start();
                    } catch (Exception e) {
                        System.out.println("start failed");
                    }
                }, 1L, TimeUnit.SECONDS);
            }
        }
    }
}
