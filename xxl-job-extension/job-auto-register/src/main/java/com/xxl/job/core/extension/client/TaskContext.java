package com.xxl.job.core.extension.client;

import com.xxl.job.core.extension.server.param.JobTask;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

/**
 * @author lesl
 */
@Component
@ConditionalOnClass(ApplicationContextAware.class)
public class TaskContext implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public <T extends Annotation> List<JobTask> findTask(Class<T> annotationClass
			, BiConsumer<T, JobTask> convertor) {
		List<JobTask> result = new ArrayList<>();

		String[] beanDefinitionNames = applicationContext
				.getBeanNamesForType(Object.class, false, true);
		for (String beanDefinitionName : beanDefinitionNames) {
			Object bean = applicationContext.getBean(beanDefinitionName);

			Map<Method, T> annotatedMethods =  MethodIntrospector
					.selectMethods(bean.getClass(), (MetadataLookup<T>) method
							-> AnnotatedElementUtils.findMergedAnnotation(method, annotationClass)
					);
			if (annotatedMethods.isEmpty()) {
				continue;
			}

			for (Entry<Method, T> methodXxlJobEntry : annotatedMethods.entrySet()) {
				Method method = methodXxlJobEntry.getKey();
				T value = methodXxlJobEntry.getValue();
				if (value != null) {
					JobTask jobTask = new JobTask();
					String classSimpleName = method.getDeclaringClass().getSimpleName();
					String methodName = method.getName();
					String jobHandler = classSimpleName + "." + methodName;
					jobTask.setJobHandler(jobHandler);
					// 因为没有默认值设置不了，所以增加默认值
					if(convertor != null) {
						convertor.accept(value, jobTask);
					}
					result.add(jobTask);
				}
			}
		}
		return result;
	}
}
