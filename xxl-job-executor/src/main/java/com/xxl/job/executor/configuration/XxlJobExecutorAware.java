package com.xxl.job.executor.configuration;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.executor.annotation.XxlJob;
import com.xxl.job.executor.factory.repository.XxlJobRepository;
import com.xxl.job.executor.utils.JobLogUtils;
import com.xxl.job.spring.boot.autoconfigure.XxlJobExecutorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * xxl-job执行器Aware
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
@Configuration
public class XxlJobExecutorAware implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

    private final XxlJobExecutorProperties xxlJobExecutorProperties;
    private final XxlJobRepository xxlJobRepository;

    private ApplicationContext applicationContext;

    public XxlJobExecutorAware(XxlJobExecutorProperties xxlJobExecutorProperties, XxlJobRepository xxlJobRepository) {
        this.xxlJobExecutorProperties = xxlJobExecutorProperties;
        this.xxlJobRepository = xxlJobRepository;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Assert.notNull(xxlJobExecutorProperties.getAdmin().getAddresses(), "'addresses' must be not null");
        Assert.notNull(xxlJobExecutorProperties.getExecutor().getAppName(), "'appName' must be not null");

        initJobHandlerMethodRepository(applicationContext);

        JobLogUtils.initLogPath(xxlJobExecutorProperties.getExecutor().getLogPath());
    }

    @Override
    public void destroy() throws Exception {
        xxlJobRepository.cleanJob();
    }

    private void initJobHandlerMethodRepository(ApplicationContext applicationContext) {

        // init job handler from method
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {

            // get bean
            Object bean = null;
            Lazy onBean = applicationContext.findAnnotationOnBean(beanDefinitionName, Lazy.class);
            if (ObjectUtil.isNotNull(onBean)){
                log.debug("job annotation scan, skip @Lazy Bean:{}", beanDefinitionName);
                continue;
            }else {
                bean = applicationContext.getBean(beanDefinitionName);
            }

            // filter method
            Map<Method, XxlJob> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        (MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));
            } catch (Throwable ex) {
                log.error("job method-job-handler resolve error for bean[" + beanDefinitionName + "].", ex);
            }
            if (CollectionUtil.isEmpty(annotatedMethods)) continue;

            // generate and register method job handler
            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                xxlJobRepository.registerJobHandler(methodXxlJobEntry.getValue(), bean, methodXxlJobEntry.getKey());
            }

        }
    }












}
