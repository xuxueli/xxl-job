package com.xxl.job.core.executor.impl;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * xxl-job executor (for spring)
 *
 * @author xuxueli 2018-11-01 09:24:52
 */
public class XxlJobSpringExecutor extends XxlJobExecutor implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobSpringExecutor.class);

    /**
     * scoped proxy 目标 Bean 定义名前缀（ScopedProxyUtils 生成，如 @RefreshScope 的 scopedTarget.refreshScopeJob）
     */
    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    // ---------------------- field ----------------------

    /**
     * excluded package, like "org.springframework"、"org.aaa,org.bbb"
     */
    private String excludedPackage = "org.springframework.,spring.";

    public void setExcludedPackage(String excludedPackage) {
        this.excludedPackage = excludedPackage;
    }


    // ---------------------- start / stop ----------------------

    /**
     * start
      */
    @Override
    public void afterSingletonsInstantiated() {

        // scan JobHandler method
        scanJobHandlerMethod(applicationContext);

        // refresh GlueFactory
        GlueFactory.refreshInstance(1);

        // super start
        try {
            super.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * stop
      */
    @Override
    public void destroy() {
        super.destroy();
    }


    /**
     * init job handler from method
     *
     * @param applicationContext applicationContext
     */
    private void scanJobHandlerMethod(ApplicationContext applicationContext) {
        // valid
        if (applicationContext == null) {
            return;
        }

        // 1、build excluded-package list
        List<String> excludedPackageList = new ArrayList<>();
        if (excludedPackage != null) {
            for (String excludedPackage : excludedPackage.split(",")) {
                if (!excludedPackage.trim().isEmpty()){
                    excludedPackageList.add(excludedPackage.trim());
                }
            }
        }

        // 2、scan bean form jobhandler
        // includeNonSingletons=true：非单例作用域的目标定义（如 @RefreshScope 生成 scopedTarget.*）也要被扫描到
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class, true, false);  // allowEagerInit=false, avoid early initialization
        for (String beanName : beanNames) {

            /**
             * 2.0、scoped proxy 结构（如 @RefreshScope 生成 scopedTarget.<name> 目标定义）：
             *      公开代理 Bean 定义交由对应的目标定义统一处理，避免同一 handler 重复注册
              */
            if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)
                    && applicationContext instanceof BeanDefinitionRegistry scopedProxyRegistry
                    && scopedProxyRegistry.containsBeanDefinition(SCOPED_TARGET_NAME_PREFIX + beanName)) {
                logger.debug(">>>>>>>>>>> xxl-job bean-definition scan, skip scoped-proxy beanName:{}", beanName);
                continue;
            }

            /**
             * 2.1、skip by BeanDefinition:
             *      - skip excluded-package bean
             *      - skip lazy-init bean
              */
            if (applicationContext instanceof BeanDefinitionRegistry beanDefinitionRegistry) {
                // get BeanDefinition
                if (!beanDefinitionRegistry.containsBeanDefinition(beanName)) {
                    continue;
                }
                BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanName);

                // skip excluded-package bean
                String beanClassName = beanDefinition.getBeanClassName();
                if (isExcluded(excludedPackageList, beanClassName)) {
                    logger.debug(">>>>>>>>>>> xxl-job bean-definition scan, skip excluded-package beanName:{}, beanClassName:{}", beanName, beanClassName);
                    continue;
                }

                // skip lazy-init bean
                if (beanDefinition.isLazyInit()) {
                    logger.debug(">>>>>>>>>>> xxl-job bean-definition scan, skip lazy-init beanName:{}", beanName);
                    continue;
                }
            }

            /**
             * 2.2、skip by BeanDefinition Class
             *      - skip beanClass is null
             *      - skip method annotation(@XxlJob) is null
             */
            Class<?> beanClass = applicationContext.getType(beanName, false);
            if (beanClass == null) {
                logger.debug(">>>>>>>>>>> xxl-job bean-definition scan, skip beanClass-null beanName:{}", beanName);
                continue;
            }
            // filter method
            Map<Method, XxlJob> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(beanClass,
                        new MethodIntrospector.MetadataLookup<XxlJob>() {
                            @Override
                            public XxlJob inspect(Method method) {
                                return AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class);
                            }
                        });
            } catch (Throwable ex) {
                logger.error(">>>>>>>>>>> xxl-job method-jobhandler resolve error for bean[" + beanName + "].", ex);
            }
            if (annotatedMethods==null || annotatedMethods.isEmpty()) {
                continue;
            }

            // 2.3、scan + registry Jobhandler
            // scoped proxy 结构（如 @RefreshScope 生成 scopedTarget.<name> 目标定义）：
            // 注册时使用公开代理 Bean，handler 每次调用经代理路由到作用域内的当前实例，保留刷新能力
            String registryBeanName = beanName;
            if (beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                String publicBeanName = beanName.substring(SCOPED_TARGET_NAME_PREFIX.length());
                if (applicationContext.containsBean(publicBeanName)) {
                    registryBeanName = publicBeanName;
                }
            }
            Object jobBean = applicationContext.getBean(registryBeanName);
            for (Map.Entry<Method, XxlJob> jobMethodEntry : annotatedMethods.entrySet()) {
                Method jobMethod = jobMethodEntry.getKey();
                XxlJob xxlJob = jobMethodEntry.getValue();
                // regist
                registryJobHandler(xxlJob, jobBean, jobMethod);
                // consider > jobhandler support Placeholders: applicationContext.getEnvironment().resolvePlaceholders(xxlJob.value());
            }

        }
    }

    /**
     * check bean if excluded
     *
     * @param excludedPackageList   excludedPackageList
     * @param beanClassName         beanClassName
     * @return  true if excluded
     */
    private boolean isExcluded(List<String> excludedPackageList, String beanClassName) {
        // excludedPackageList is empty, no excluded
        if (excludedPackageList == null || excludedPackageList.isEmpty()) {
            return false;
        }

        // beanClassName is null, no excluded
        if (beanClassName == null) {
            return false;
        }

        // excludedPackageList match, excluded (not scan)
        for (String excludedPackage : excludedPackageList) {
            if (beanClassName.startsWith(excludedPackage)) {
                return true;
            }
        }
        return false;
    }


    // ---------------------- applicationContext ----------------------
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        XxlJobSpringExecutor.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
