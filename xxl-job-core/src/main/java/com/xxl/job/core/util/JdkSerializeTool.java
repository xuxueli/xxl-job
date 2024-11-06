package com.xxl.job.core.util;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuxueli 2020-04-12 0:14:00
 */
public class JdkSerializeTool {

	private static final Logger logger = LoggerFactory.getLogger(JdkSerializeTool.class);

	// ------------------------ serialize and unserialize ------------------------

	/**
	 * 将对象-->byte[] (由于jedis中不支持直接存储object所以转换成byte[]存入)
	 */
	public static byte[] serialize(Object object) {
		// 序列化
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(object);
			return baos.toByteArray();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 将byte[] -->Object
	 */
	public static <T> Object deserialize(byte[] bytes, Class<T> clazz) {
		// 反序列化
		final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try (ObjectInputStream ois = new ObjectInputStream(bais)) {
			return ois.readObject();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
