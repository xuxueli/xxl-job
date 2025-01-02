package com.xxl.job.core.glue.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.annotation.Resource;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.glue.GlueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author xuxueli 2018-11-01
 */
public class SpringGlueFactory extends GlueFactory {

	private static final Logger logger = LoggerFactory.getLogger(SpringGlueFactory.class);

	/**
	 * inject action of spring
	 */
	@Override
	public void injectService(Object instance) {
		if (instance == null) {
			return;
		}

		final ApplicationContext context = XxlJobSpringExecutor.getApplicationContext();
		if (context == null) {
			return;
		}

		Field[] fields = instance.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			Object fieldBean = null;
			// with bean-id, bean could be found by both @Resource and @Autowired, or bean could only be found by @Autowired

			final Resource resource = AnnotationUtils.getAnnotation(field, Resource.class);
			if (resource != null) {
				try {
					if (!resource.name().isEmpty()) {
						fieldBean = context.getBean(resource.name());
					} else {
						fieldBean = context.getBean(field.getName());
					}
				} catch (Exception ignored) {
				}
				if (fieldBean == null) {
					fieldBean = context.getBean(field.getType());
				}
			} else if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
				Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
				if (qualifier != null && !qualifier.value().isEmpty()) {
					fieldBean = context.getBean(qualifier.value());
				} else {
					fieldBean = context.getBean(field.getType());
				}
			}

			if (fieldBean != null) {
				field.setAccessible(true);
				try {
					field.set(instance, fieldBean);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

}