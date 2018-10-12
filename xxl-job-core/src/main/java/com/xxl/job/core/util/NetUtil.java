package com.xxl.job.core.util;

import java.io.*;
import java.net.*;

/**
 * net util
 *
 * @author xuxueli 2017-11-29 17:00:25
 */
public class NetUtil {

    /**
     * 获取可用的端口号, 从 defaultPort [1025] 开始查找
     *
     * @param defaultPort the default port 默认端口号
     * @return int int
     */
    public static int findAvailablePort(int defaultPort) {
        // 不使用 0~1024 之间的端口
        int portTmp = defaultPort <= 1024 ? 1025 : defaultPort;
        try {
            return getRangePort(portTmp, Short.MAX_VALUE * 2);
        } catch (IllegalStateException e) {
            return getRangePort(1025, portTmp);
        }
    }

    private static int getRangePort(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port ++) {
            try (ServerSocket socket = new ServerSocket(port)) {
                return socket.getLocalPort();
            } catch (IOException ignored) {
            }
        }
        throw new IllegalStateException("no free port found");
    }
}
