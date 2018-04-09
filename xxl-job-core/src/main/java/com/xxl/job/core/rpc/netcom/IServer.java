package com.xxl.job.core.rpc.netcom;

/**
 * rpc server interface
 *
 * @author zixiao
 * @date 18/4/4
 */
public interface IServer {

    void start(final int port, final String ip, final String appName) throws Exception;

    void destroy();
}
