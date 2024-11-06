package com.xxl.job.admin.core.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jackson util
 * <p>
 * 1、obj need private and set/get；
 * 2、do not support inner class；
 *
 * @author xuxueli 2015-9-25 18:02:56
 */
public class JacksonUtil {

	private static Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

	private final static ObjectMapper objectMapper = new ObjectMapper();

	public static ObjectMapper getInstance() {
		return objectMapper;
	}

	/**
	 * bean、array、List、Map --> json
	 *
	 * @return json string
	 */
	public static String writeValueAsString(Object obj) {
		try {
			return getInstance().writeValueAsString(obj);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * string --> bean、Map、List(array)
	 *
	 * @return obj
	 */
	public static <T> T readValue(String jsonStr, Class<T> clazz) {
		try {
			return getInstance().readValue(jsonStr, clazz);
		} catch (IOException e) { // JsonParseException、JsonMappingException 都继承自 IOException
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * string --> List<Bean>...
	 */
	public static <T> T readValue(String jsonStr, Class<?> parametrized, Class<?>... parameterClasses) {
		try {
			JavaType javaType = getInstance().getTypeFactory().constructParametricType(parametrized, parameterClasses);
			return getInstance().readValue(jsonStr, javaType);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
