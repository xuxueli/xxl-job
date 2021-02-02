package com.xxl.job.core.extension.client;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.extension.server.param.JobAutoRegisterParam;
import com.xxl.job.core.extension.server.param.JobTask;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author lesl
 */
@ConditionalOnBean(XxlJobExecutor.class)
public class RegisterClient implements SmartInitializingSingleton {

	private final Map<Class<? extends Annotation>, BiConsumer<?, JobTask>> annotations;

	private final TaskContext taskContext;

	private final XxlJobExecutor xxlJobExecutor;

	private final String appName;

	private final String appTitle;

	public RegisterClient(String appName, String appTitle, TaskContext taskContext,
			XxlJobExecutor xxlJobExecutor) {
		this.appName = appName;
		this.appTitle = appTitle;
		this.annotations = new HashMap<>();
		this.taskContext = taskContext;
		this.xxlJobExecutor = xxlJobExecutor;

		//设置XXL-Job的转换
		putAnnotation(XxlJob.class, null);
	}

	public void enableScheduleSupport() {
		//设置Spring Scheduled的转换
		putAnnotation(Scheduled.class, (input, output) -> {
			String cron = input.cron();
			if (!"".equals(cron)) {
				output.setCron(cron);
			} else {
				long fixedRate = input.fixedRate();
				long fixedDelay = input.fixedDelay();
				// 是否应该转换一下?
			}
		});
	}

	public <T extends Annotation> void putAnnotation(Class<T> annotationClass,
			BiConsumer<T, JobTask> convertor) {
		this.annotations.put(annotationClass, convertor);
	}

	@SneakyThrows
	@Override
	public void afterSingletonsInstantiated() {

		// 获取@Schedule, @XXlJob注解列表去注册
		if (CollectionUtils.isEmpty(annotations)) {
			return;
		}
		List<JobTask> allTaskList = findAllTaskList();

		String address = getFieldValue(xxlJobExecutor, "adminAddresses");
		String accessToken = getFieldValue(xxlJobExecutor, "accessToken");

		JobAutoRegisterParam autoRegisterParam = new JobAutoRegisterParam();
		autoRegisterParam.setJobTasks(allTaskList);
		autoRegisterParam.setAppName(appName);
		autoRegisterParam.setAppTitle(appTitle);

		XxlJobRemotingUtil
				.postBody(address + "/api/extension/job-register", accessToken, 3, autoRegisterParam,
						String.class);
	}

	@SneakyThrows
	private String getFieldValue(XxlJobExecutor xxlJobExecutor, String fieldName) {
		Field field = ReflectionUtils.findField(xxlJobExecutor.getClass(), fieldName);
		assert field != null;
		ReflectionUtils.makeAccessible(field);
		return (String) field.get(xxlJobExecutor);
	}

	@SuppressWarnings("unchecked")
	private List<JobTask> findAllTaskList() {
		Stream<List<JobTask>> listStream = annotations.entrySet()
				.parallelStream()
				.map(entry -> {
					Class key = entry.getKey();
					BiConsumer value = entry.getValue();
					return (List<JobTask>) taskContext.findTask(key, value);
				});

		return listStream.flatMap(Collection::stream).collect(Collectors.toList());
	}
}
