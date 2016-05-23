package com.xxl.job.admin.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * properties util
 * @author xuxueli 2015-8-28 10:35:53
 */
public class PropertiesUtil {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	private static final String file_name = "config.properties";
	
	/**
	 * load properties
	 * @param propertyFileName
	 * @param ifClassPath
	 * @return
	 */
	public static Properties loadProperties(String propertyFileName) {
		Properties prop = new Properties();
		InputStreamReader  in = null;
		try {
			URL url = null;
			ClassLoader loder = Thread.currentThread().getContextClassLoader();
			url = loder.getResource(propertyFileName); 
			in = new InputStreamReader(new FileInputStream(url.getPath()), "UTF-8");
			prop.load(in);
		} catch (IOException e) {
			logger.error("load {} error!", propertyFileName);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("close {} error!", propertyFileName);
				}
			}
		}
		return prop;
	}

	public static String getString(String key) {
		Properties prop = loadProperties(file_name);
		if (prop!=null) {
			return prop.getProperty(key);
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(getString("triggerLogUrl"));
	}

}
