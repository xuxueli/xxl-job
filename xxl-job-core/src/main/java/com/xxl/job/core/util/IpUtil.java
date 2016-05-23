package com.xxl.job.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * get ip
 * @author xuxueli 2016-5-22 11:38:05
 */
public class IpUtil {
	private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

	/**
	 * 获取本机ip
	 * @return
	 */
	public static String getIp() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress address = null;
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				Enumeration<InetAddress> addresses = ni.getInetAddresses();
				while (addresses.hasMoreElements()) {
					address = addresses.nextElement();
					if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
						return address.getHostAddress();
					}
				}
			}
			logger.info("xxl job getHostAddress fail");
			return null;
		} catch (Throwable t) {
			logger.error("xxl job getHostAddress error, {}", t);
			return null;
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		System.out.println(InetAddress.getLocalHost().getCanonicalHostName());
		System.out.println(InetAddress.getLocalHost().getHostName());
		System.out.println(getIp());
	}

}
