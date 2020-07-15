package com.xxl.job.admin.core.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 机器工具类
 * @author 单红宇
 *
 */
public class MachineUtils {

	private static final Logger logger = LoggerFactory.getLogger(MachineUtils.class);

	private static String machineIp = null;
	
	public static String getIPAndPort(String port) {
		return getIP().concat(":").concat(port);
	}

	public static String getIP() {
		if (machineIp == null) {
			String ipv4 = getInet4Address();
			logger.info("ipv4={}", ipv4);
			machineIp = ipv4;
		}
		return machineIp;
	}

	/**
	 * 获取服务器Ipv4地址
	 */
	public static String getInet4Address() {
		Enumeration<NetworkInterface> nis;
		String ip = null;
		try {
			nis = NetworkInterface.getNetworkInterfaces();
			for (; nis.hasMoreElements();) {
				NetworkInterface ni = nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				for (; ias.hasMoreElements();) {
					InetAddress ia = ias.nextElement();
					// ia instanceof Inet6Address && !ia.equals("")
					if (ia instanceof Inet4Address && !ia.getHostAddress().equals("127.0.0.1")) {
						ip = ia.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			logger.error("getServerIpAddress执行出错：" + e.getMessage() + "," + e.getCause());
		}
		return ip;
	}

}
