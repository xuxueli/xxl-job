package com.xxl.job.core.glue;

import com.xxl.job.core.glue.cache.LocalCache;
import com.xxl.job.core.glue.loader.GlueLoader;
import com.xxl.job.core.handler.IJobHandler;
import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * glue factory, product class/object by name
 * @author xuxueli 2016-1-2 20:02:27
 */
public class GlueFactory implements ApplicationContextAware {
	private static Logger logger = LoggerFactory.getLogger(GlueFactory.class);
	
	/**
	 * groovy class loader
	 */
	private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
	
	/**
	 * glue cache timeout / second
	 */
	private long cacheTimeout = 5000;
	public void setCacheTimeout(long cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}
	
	/**
	 * code source loader
	 */
	private GlueLoader glueLoader;
	public void setGlueLoader(GlueLoader glueLoader) {
		this.glueLoader = glueLoader;
	}
	public static boolean isActive() {
		return GlueFactory.glueFactory.glueLoader!=null;
	}

	// ----------------------------- spring support -----------------------------
	private static ApplicationContext applicationContext;
	private static GlueFactory glueFactory;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		GlueFactory.applicationContext = applicationContext;
		GlueFactory.glueFactory = (GlueFactory) applicationContext.getBean("glueFactory");
	}
	
	/**
	 * inject action of spring
	 * @param instance
	 */
	public void injectService(Object instance){
		if (instance==null) {
			return;
		}
	    
		Field[] fields = instance.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
			Object fieldBean = null;
			// with bean-id, bean could be found by both @Resource and @Autowired, or bean could only be found by @Autowired
			if (AnnotationUtils.getAnnotation(field, Resource.class) != null) {
				try {
					fieldBean = applicationContext.getBean(field.getName());
				} catch (Exception e) {
				}
				if (fieldBean==null ) {
					fieldBean = applicationContext.getBean(field.getType());
				}
			} else if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
				fieldBean = applicationContext.getBean(field.getType());		
			}
			
			if (fieldBean!=null) {
				field.setAccessible(true);
				try {
					field.set(instance, fieldBean);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// ----------------------------- load instance -----------------------------
	// load new instance, prototype
	public IJobHandler loadNewInstance(String job_group, String job_name) throws Exception{
		if (job_group==null || job_group.trim().length()==0 || job_name==null || job_name.trim().length()==0) {
			return null;
		}
		String codeSource = glueLoader.load(job_group, job_name);
		if (codeSource!=null && codeSource.trim().length()>0) {
			Class<?> clazz = groovyClassLoader.parseClass(codeSource);
			if (clazz != null) {
				Object instance = clazz.newInstance();
				if (instance!=null) {
					if (instance instanceof IJobHandler) {
						this.injectService(instance);
						return (IJobHandler) instance;
					} else {
						throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, "
								+ "cannot convert from instance["+ instance.getClass() +"] to IJobHandler");
					}
				}
			}
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
	}
	
	// // load instance, singleton
	private static String generateInstanceCacheKey(String job_group, String job_name){
		return job_group.concat("_").concat(job_name).concat("_instance");
	}
	public IJobHandler loadInstance(String job_group, String job_name) throws Exception{
		if (job_group==null || job_group.trim().length()==0 || job_name==null || job_name.trim().length()==0) {
			return null;
		}
		String cacheInstanceKey = generateInstanceCacheKey(job_group, job_name);
		Object cacheInstance = LocalCache.getInstance().get(cacheInstanceKey);
		if (cacheInstance!=null) {
			if (!(cacheInstance instanceof IJobHandler)) {
				throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadInstance error, "
						+ "cannot convert from cacheClass["+ cacheInstance.getClass() +"] to IJobHandler");
			}
			return (IJobHandler) cacheInstance;
		}
		Object instance = loadNewInstance(job_group, job_name);
		if (instance!=null) {
			if (!(instance instanceof IJobHandler)) {
				throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadInstance error, "
						+ "cannot convert from instance["+ instance.getClass() +"] to IJobHandler");
			}
			
			LocalCache.getInstance().set(cacheInstanceKey, instance, cacheTimeout);
			logger.info(">>>>>>>>>>>> xxl-glue, fresh instance, cacheInstanceKey:{}", cacheInstanceKey);
			return (IJobHandler) instance;
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadInstance error, instance is null");
	}
	
	// ----------------------------- util -----------------------------
	public static void glue(String job_group, String job_name, String... params) throws Exception{
		GlueFactory.glueFactory.loadInstance(job_group, job_name).execute(params);
	}
	
}
