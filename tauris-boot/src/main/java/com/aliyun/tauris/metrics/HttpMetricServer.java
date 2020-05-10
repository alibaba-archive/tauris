package com.aliyun.tauris.metrics;

import com.aliyun.tauris.TLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetAddress;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class HttpMetricServer extends MetricServer {

    private TLogger logger;

    /**
     * 绑定的IP地址, 如192.168.1.1, 默认是127.0.0.1
     */
    private String host = "127.0.0.1";

    private int port = 0;

    private String path = "/metrics";

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public HttpMetricServer(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.logger = TLogger.getLogger(this);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 通过VM参数创建MetricServer
     *
     * @return
     */
    public static HttpMetricServer createMetricServer() {
        String pt   = System.getProperty("tauris.metric.port");
        String path = System.getProperty("tauris.metric.path", "/metrics");
        String host = System.getProperty("tauris.metric.host", "127.0.0.1");
        if (pt != null) {
            try {
                return new HttpMetricServer(host, Integer.parseInt(pt), path);
            } catch (NumberFormatException e) {
                System.err.println("invalid metric port " + pt);
            }
        }
        return null;
    }

    @Override
    public void init() {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline()
                                .addLast("decoder", new HttpRequestDecoder())
                                .addLast("encoder", new HttpResponseEncoder())
                                .addLast("handler", new HttpMetricsHandler(CollectorRegistry.defaultRegistry, path));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
    }

    @Override
    public void start() {
        this.init();
        try {
            ChannelFuture f = bootstrap.bind(InetAddress.getByName(host), port).sync();
            f.channel().closeFuture();
            logger.INFO("metric server started, {%s:%s}", host, port);
        } catch (Exception e) {
            System.err.println("metric server start failed, cause by " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void shutdown() {
        try {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        } catch (Exception e) {
            logger.ERROR(e);
        }
    }
}

