package com.xxl.job.core.rpc.netcom.netty.server;

import com.xxl.job.core.rpc.netcom.IServer;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.thread.TriggerCallbackThread;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈rpc netty server〉<p>
 *
 * @author zixiao
 * @date 18/4/4
 */
public class NettyServer implements IServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    private Thread thread;

    public NettyServer(){
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    @Override
    public void start(final int port, final String ip, final String appName) throws Exception {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline()
                                        .addLast(new HttpResponseEncoder())
                                        .addLast(new HttpRequestDecoder())
                                        .addLast(new HttpObjectAggregator(1024*1024))
                                        .addLast(new NettyServerHandler());// 服务端业务逻辑
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                try {
                    // Start Server
                    ChannelFuture future = bootstrap.bind(port).sync();
                    logger.info(">>>>>>>>>>> xxl-job netty server start success at port:{}.", port);

                    // Start Registry-Server
                    ExecutorRegistryThread.getInstance().start(port, ip, appName);

                    // Start Callback-Server
                    TriggerCallbackThread.getInstance().start();

                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    destroy();
                }
            }
        });
        thread.setDaemon(true);	// daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
        thread.start();
    }

    @Override
    public void destroy() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
