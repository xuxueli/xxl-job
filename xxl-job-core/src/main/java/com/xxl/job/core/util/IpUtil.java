package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * get ip
 *
 * @author xuxueli 2016-5-22 11:38:05
 */
public class IpUtil {
	private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

	private static final String ANYHOST = "0.0.0.0";
	private static final String LOCALHOST = "127.0.0.1";
	public static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

	private static volatile String LOCAL_ADDRESS = null;

	/**
	 * valid address
	 * @param address
	 * @return boolean
	 */
	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress() || address.isLinkLocalAddress()) {
			return false;
		}
		String name = address.getHostAddress();
		return (name != null
				&& ! ANYHOST.equals(name)
				&& ! LOCALHOST.equals(name)
				&& IP_PATTERN.matcher(name).matches());
	}

	/**
	 * get first valid addredd
	 *
	 * @return InetAddress
	 */
	private static InetAddress getFirstValidAddress() {

		// NetworkInterface address
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces != null) {
				while (interfaces.hasMoreElements()) {
					try {
						NetworkInterface network = interfaces.nextElement();
						Enumeration<InetAddress> addresses = network.getInetAddresses();
						if (addresses != null) {
							while (addresses.hasMoreElements()) {
								try {
									InetAddress address = addresses.nextElement();
									if (isValidAddress(address)) {
										return address;
									}
								} catch (Throwable e) {
									logger.error("Failed to retriving ip address, " + e.getMessage(), e);
								}
							}
						}
					} catch (Throwable e) {
						logger.error("Failed to retriving ip address, " + e.getMessage(), e);
					}
				}
			}
		} catch (Throwable e) {
			logger.error("Failed to retriving ip address, " + e.getMessage(), e);
		}

		// getLocalHost address
		try {
			InetAddress localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress;
			}
		} catch (Throwable e) {
			logger.error("Failed to retriving ip address, " + e.getMessage(), e);
		}
		
		logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
		return null;
	}
	

	/**
	 * get address
	 *
	 * @return String
	 */
	private static String getAddress() {
		if (LOCAL_ADDRESS != null) {
			return LOCAL_ADDRESS;
		}
		InetAddress localAddress = getFirstValidAddress();
		LOCAL_ADDRESS = localAddress.getHostAddress();
		return LOCAL_ADDRESS;
	}

	/**
	 * get ip
	 *
	 * @return String
	 */
	public static String getIp(){
		return getAddress();
	}

	/**
	 * get ip:port
	 *
	 * @param port
	 * @return String
	 */
	public static String getIpPort(int port){
		String ip = getIp();
		if (ip==null) {
			return null;
		}
		return ip.concat(":").concat(String.valueOf(port));
	}

	public static void main(String[] args) throws UnknownHostException {
		System.out.println(getIp());
		System.out.println(getIpPort(8080));
	}

}
