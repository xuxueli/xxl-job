package com.xxl.job.core.util;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * net util
 *
 * @author xuxueli 2017-11-29 17:00:25
 */
public class NetUtil {

	private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

	/**
	 * find avaliable port
	 */
	public static int findAvailablePort(int defaultPort) {
		int port = defaultPort;
		while (port < 65535) {
			if (isPortAvailable(port)) {
				return port;
			}
			port++;
		}
		port = defaultPort - 1;
		while (port > 0) {
			if (isPortAvailable(port)) {
				return port;
			}
			port--;
		}
		throw new RuntimeException("no available port.");
	}

	/**
	 * check port used
	 */
	public static boolean isPortAvailable(int port) {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			return true;
		} catch (IOException ignored) {
			logger.info(">>>>>>>>>>> xxl-job, port[{}] is in use.", port);
			return false;
		}
	}

}
