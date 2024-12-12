package com.xxl.job.core.glue;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.glue.impl.SpringGlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import groovy.lang.GroovyClassLoader;
import org.springframework.util.StringUtils;

/**
 * glue factory, product class/object by name
 *
 * @author xuxueli 2016-1-2 20:02:27
 */
public class GlueFactory {

	private static GlueFactory glueFactory;

	public static GlueFactory getInstance() {
		GlueFactory factory = glueFactory;
		if (XxlJobSpringExecutor.groovyRefreshRequired) {
			synchronized (GlueFactory.class) {
				if (XxlJobSpringExecutor.groovyRefreshRequired) {
					glueFactory = factory = new SpringGlueFactory();
					XxlJobSpringExecutor.groovyRefreshRequired = false;
				} else {
					factory = glueFactory;
				}
			}
		}
		if (factory == null) {
			glueFactory = factory = new GlueFactory();
		}
		return factory;
	}

	/**
	 * groovy class loader
	 */
	private final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
	private final ConcurrentMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

	/**
	 * load new instance, prototype
	 */
	public IJobHandler loadNewInstance(String codeSource) throws Exception {
		if (StringUtils.hasText(codeSource)) {
			Class<?> clazz = getCodeSourceClass(codeSource);
			if (clazz != null) {
				Object instance = clazz.newInstance(); // instance 不会为 null
				if (instance instanceof IJobHandler) {
					this.injectService(instance);
					return (IJobHandler) instance;
				} else {
					throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, cannot convert from instance[" + instance.getClass() + "] to IJobHandler");
				}
			}
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
	}

	private Class<?> getCodeSourceClass(String codeSource) {
		try {
			// md5
			byte[] md5 = MessageDigest.getInstance("MD5").digest(codeSource.getBytes());
			String md5Str = new BigInteger(1, md5).toString(16);

			Class<?> clazz = CLASS_CACHE.get(md5Str);
			if (clazz == null) {
				clazz = groovyClassLoader.parseClass(codeSource);
				CLASS_CACHE.putIfAbsent(md5Str, clazz);
			}
			return clazz;
		} catch (Exception e) {
			return groovyClassLoader.parseClass(codeSource);
		}
	}

	/**
	 * inject service of bean field
	 */
	public void injectService(Object instance) {
		// do something
	}

}