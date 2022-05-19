package com.xxl.job.core.server.factory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;



public class NettyEventLoopFactory {


    private static final String NETTY_EPOLL_ENABLE_KEY = "netty.epoll.enable";

    private static final String OS_NAME_KEY = "os.name";

    private static final String OS_LINUX_PREFIX = "linux";


    public static EventLoopGroup eventLoopGroup(int threads) {
        return shouldEpoll() ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
    }


    public static Class<? extends ServerSocketChannel> serverSocketChannelClass() {
        return shouldEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    private static boolean shouldEpoll() {
        if (Boolean.parseBoolean(System.getProperty(NETTY_EPOLL_ENABLE_KEY, "false"))) {
            String osName = System.getProperty(OS_NAME_KEY);
            return osName.toLowerCase().contains(OS_LINUX_PREFIX) && Epoll.isAvailable();
        }
        return false;
    }
}
