package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * net util
 *
 * @author xuxueli 2017-11-29 17:00:25
 */
public class NetUtil {
    private static Logger logger = LoggerFactory.getLogger(NetUtil.class);

    /**
     * find avaliable port by ip
     *
     * @param defaultPort
     * @param ip
     * @return
     */
    public static int findAvailablePort(int defaultPort,String ip) {
        int portTmp = defaultPort;
        while (portTmp < 65535) {
            if (!isPortUsed(portTmp,ip)) {
                return portTmp;
            } else {
                portTmp++;
            }
        }
        portTmp = --defaultPort;
        while (portTmp > 0) {
            if (!isPortUsed(portTmp,ip)) {
                return portTmp;
            } else {
                portTmp--;
            }
        }
        throw new IllegalStateException("no available port.");
    }

    /**
     * find avaliable port
     *
     * @param defaultPort
     * @return
     */
    public static int findAvailablePort(int defaultPort) {
        return findAvailablePort(defaultPort,null);
    }


    /**
     * check port used
     *
     * @param port
     * @param ip 为空则为 InetAddress.anyLocalAddress()
     * @return
     */
    public static boolean isPortUsed(int port,String ip) {
        boolean used = false;
        ServerSocket serverSocket = null;
        try {
            if(StringUtils.isEmpty(ip)){
                serverSocket = new ServerSocket(port);
            }else {
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(ip,port));
            }
        } catch (IOException e) {
            logger.debug(">>>>>>>>>>> xxl-job, port[{}] is in use.", port);
            used = true;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.info("");
                }
            }
        }
        return used;
    }

}
