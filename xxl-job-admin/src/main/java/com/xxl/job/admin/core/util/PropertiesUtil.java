package com.xxl.job.admin.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * properties util
 *
 * @author xuxueli 2015-8-28 10:35:53
 */
public class PropertiesUtil {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	private static final String file_name = "xxl-job-admin.properties";


	public static String getString(String key) {
		Properties prop = null;
		try {
			Resource resource = new ClassPathResource(file_name);
			EncodedResource encodedResource = new EncodedResource(resource,"UTF-8");
			prop = PropertiesLoaderUtils.loadProperties(encodedResource);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (prop!=null) {
			return prop.getProperty(key);
		}
		return null;
	}

}
