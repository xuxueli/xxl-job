package com.xxl.job.core.server;

import com.xxl.job.core.thread.ExecutorRegistryThread;

/**
 * @author SongLongKuan
 * @time 2021/2/22 10:02 上午
 */
public interface AbstractServer {


    /**
     * 启动服务
     *
     * @param address     : 当前服务地址
     * @param port        : 端口
     * @param appname     : 当前服务名称
     * @param accessToken :  授权令牌
     * @author: SongLongKuan
     * @date: 2021-02-22 10:04 上午
     * @return: void
     */
    void start(final String address, final int port, final String appname, final String accessToken);


    /**
     * 停止服务
     *
     * @author: SongLongKuan
     * @date: 2021-02-22 10:04 上午
     * @return: void
     */
    void stop() throws Exception;


    /**
     * 启动注册
     * @param appname : 应用名称
     * @param address : 应用地址
     * @author: SongLongKuan
     * @date: 2021-02-22 10:15 上午
     * @return: void
     */
    default void startRegistry(final String appname, final String address) {
        // start registry
        ExecutorRegistryThread.getInstance().start(appname, address);
    }

    /**
     * 停止注册
     */
    default void stopRegistry() {
        // stop registry
        ExecutorRegistryThread.getInstance().toStop();
    }
}
