package com.xxl.job.core.server;

/**
 * @author SongLongKuan
 * @time 2021/2/22 10:12 上午
 * Spring 容器下不用netty之类的框架启动端口，可以直接使用spring boot的端口
 * 这里呢只是单纯的做一些注册操作，向xxladmin注册
 */
public class SpringServer implements AbstractServer {
    @Override
    public void start(String address, int port, String appname, String accessToken) {
        startRegistry(appname, address);
    }

    @Override
    public void stop() throws Exception {
        stopRegistry();
    }
}
