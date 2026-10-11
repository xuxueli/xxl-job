package com.xxl.job.core.glue;

import com.xxl.job.core.glue.impl.SpringGlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.tool.core.StringTool;
import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * glue factory, product class/object by name
 *
 * @author xuxueli 2016-1-2 20:02:27
 */
public class GlueFactory {
	private static final Logger logger = LoggerFactory.getLogger(GlueFactory.class);


	private static GlueFactory glueFactory = new GlueFactory();
	public static GlueFactory getInstance(){
		return glueFactory;
	}

	/**
	 * refresh instance by type
	 *
	 * @param type		0-frameless, 1-spring;
	 */
	public static void refreshInstance(int type){
		if (type == 0) {
			glueFactory = new GlueFactory();
		} else if (type == 1) {
			glueFactory = new SpringGlueFactory();
		}
	}


	/**
	 * current glue class cache: jobId -> latest version only.
	 * Each version uses an isolated GroovyClassLoader so outdated classes become unloadable
	 * after the old JobThread releases its handler instance.
	 */
	private final ConcurrentMap<Integer, GlueClassHolder> jobClassCache = new ConcurrentHashMap<>();

	/**
	 * load new instance for a job, prototype. Keeps only the latest class version per jobId.
	 *
	 * @param jobId       job id
	 * @param codeSource  code source
	 * @return IJobHandler
	 */
	public IJobHandler loadNewInstance(int jobId, String codeSource) throws Exception{
		if (StringTool.isBlank(codeSource)) {
			throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
		}

		String md5Str = md5(codeSource);
		GlueClassHolder existing = jobClassCache.get(jobId);
		if (existing != null && md5Str.equals(existing.md5)) {
			return newInstance(existing.clazz);
		}

		GroovyClassLoader classLoader = new GroovyClassLoader(GlueFactory.class.getClassLoader());
		Class<?> clazz;
		try {
			clazz = classLoader.parseClass(codeSource);
		} catch (Exception e) {
			closeClassLoader(classLoader);
			logger.error(">>>>>>>>>>> xxl-glue, loadNewInstance error, jobId:{}", jobId, e);
			throw e;
		}

		GlueClassHolder created = new GlueClassHolder(md5Str, clazz, classLoader);
		GlueClassHolder holder = jobClassCache.compute(jobId, (id, prev) -> {
			if (prev != null && md5Str.equals(prev.md5)) {
				return prev;
			}
			return created;
		});
		if (holder != created) {
			// another thread already cached the same version
			closeClassLoader(classLoader);
		}
		return newInstance(holder.clazz);
	}

	/**
	 * load new instance, prototype.
	 * <p>
	 * Prefer {@link #loadNewInstance(int, String)} in the executor trigger path so outdated
	 * classes can be unloaded. This overload loads with a one-off ClassLoader and does not cache.
	 *
	 * @param codeSource  code source
	 * @return IJobHandler
	 */
	public IJobHandler loadNewInstance(String codeSource) throws Exception{
		if (StringTool.isBlank(codeSource)) {
			throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
		}

		GroovyClassLoader classLoader = new GroovyClassLoader(GlueFactory.class.getClassLoader());
		try {
			Class<?> clazz = classLoader.parseClass(codeSource);
			return newInstance(clazz);
		} catch (Exception e) {
			closeClassLoader(classLoader);
			logger.error(">>>>>>>>>>> xxl-glue, loadNewInstance error", e);
			throw e;
		} catch (Error e) {
			closeClassLoader(classLoader);
			logger.error(">>>>>>>>>>> xxl-glue, loadNewInstance error", e);
			throw e;
		}
	}

	/**
	 * drop cached glue class for a job (e.g. when JobThread is removed).
	 * Does not close the ClassLoader immediately; in-flight handlers may still reference it.
	 *
	 * @param jobId job id
	 */
	public void clear(int jobId) {
		jobClassCache.remove(jobId);
	}

	/**
	 * visible for tests
	 */
	int cacheSize() {
		return jobClassCache.size();
	}

	/**
	 * visible for tests
	 */
	ClassLoader cachedClassLoader(int jobId) {
		GlueClassHolder holder = jobClassCache.get(jobId);
		return holder != null ? holder.classLoader : null;
	}

	/**
	 * visible for tests
	 */
	Class<?> cachedClass(int jobId) {
		GlueClassHolder holder = jobClassCache.get(jobId);
		return holder != null ? holder.clazz : null;
	}

	private IJobHandler newInstance(Class<?> clazz) throws Exception {
		Object instance = clazz.getDeclaredConstructor().newInstance();
		if (instance instanceof IJobHandler) {
			this.injectService(instance);
			return (IJobHandler) instance;
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, "
				+ "cannot convert from instance[" + instance.getClass() + "] to IJobHandler");
	}

	private static String md5(String codeSource) throws Exception {
		byte[] md5 = MessageDigest.getInstance("MD5").digest(codeSource.getBytes(StandardCharsets.UTF_8));
		return new BigInteger(1, md5).toString(16);
	}

	private static void closeClassLoader(GroovyClassLoader classLoader) {
		try {
			classLoader.close();
		} catch (Exception e) {
			logger.error(">>>>>>>>>>> xxl-glue, close ClassLoader error", e);
		}
	}

	private static final class GlueClassHolder {
		private final String md5;
		private final Class<?> clazz;
		private final GroovyClassLoader classLoader;

		private GlueClassHolder(String md5, Class<?> clazz, GroovyClassLoader classLoader) {
			this.md5 = md5;
			this.clazz = clazz;
			this.classLoader = classLoader;
		}
	}

	/**
	 * inject service of bean field
	 *
	 * @param instance  instance
	 */
	public void injectService(Object instance) {
		// do something
	}

}
