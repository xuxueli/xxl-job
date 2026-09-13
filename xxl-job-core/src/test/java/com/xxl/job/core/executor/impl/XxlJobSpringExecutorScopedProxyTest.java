package com.xxl.job.core.executor.impl;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.junit.jupiter.api.Test;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.config.Scope;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * scoped proxy（如 spring-cloud 的 @RefreshScope）Bean 中的 @XxlJob 方法应能被扫描并注册：
 * 扫描时使用目标 Bean 类型，注册时持有公开代理 Bean，保留作用域刷新能力。
 */
public class XxlJobSpringExecutorScopedProxyTest {

    @Test
    public void scanJobHandler_shouldRegisterHandlerInScopedProxyBean() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getBeanFactory().registerScope("refresh", new SimpleMapScopeImpl());
        context.register(ExecutorConfig.class, RefreshScopeProxyCreator.class);
        context.refresh();
        try {
            XxlJobSpringExecutor executor = context.getBean(XxlJobSpringExecutor.class);

            // scoped proxy Bean 中的 handler 应被注册
            assertNotNull(executor.loadJobHandler("refreshScopeJob"),
                    "handler in scoped proxy bean should be registered");

            // 普通单例 Bean 的注册逻辑不受影响
            assertNotNull(executor.loadJobHandler("plainScopeJob"),
                    "handler in plain singleton bean should be registered");

            // 注册的 handler 应持有公开代理 Bean（刷新作用域后仍路由到当前实例），而不是裸的目标实例
            Object handler = executor.loadJobHandler("refreshScopeJob");
            Object handlerTarget = readHandlerTarget(handler);
            Object proxyBean = context.getBean("refreshScopeJob");
            Object rawTargetBean = context.getBean("scopedTarget.refreshScopeJob");
            assertEquals(proxyBean, handlerTarget, "handler should hold the public scoped proxy");
            assertTrue(handlerTarget != rawTargetBean, "handler should not hold the raw scoped target instance");
        } finally {
            context.close();
        }
    }

    /** 反射读取 MethodJobHandler 持有的目标 Bean */
    private Object readHandlerTarget(Object handler) throws Exception {
        for (Class<?> clazz = handler.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field targetField = clazz.getDeclaredField("target");
                targetField.setAccessible(true);
                return targetField.get(handler);
            } catch (NoSuchFieldException e) {
                // continue up the hierarchy
            }
        }
        throw new NoSuchFieldException("target not found on " + handler.getClass());
    }

    /** 最小化 Scope 实现：每个作用域名缓存一个实例 */
    private static class SimpleMapScopeImpl implements Scope {
        private final java.util.Map<String, Object> instances = new java.util.HashMap<>();

        @Override
        public Object get(String name, org.springframework.beans.factory.ObjectFactory<?> objectFactory) {
            return instances.computeIfAbsent(name, key -> objectFactory.getObject());
        }

        @Override
        public Object remove(String name) {
            return instances.remove(name);
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
        }

        @Override
        public Object resolveContextualObject(String key) {
            return null;
        }

        @Override
        public String getConversationId() {
            return "refresh";
        }
    }

    /** 模拟 @RefreshScope 的 Bean 定义结构：原 Bean 变为 scoped proxy，真实定义移至 scopedTarget.<name> */
    public static class RefreshScopeProxyCreator implements BeanDefinitionRegistryPostProcessor {

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
            BeanDefinition definition = registry.getBeanDefinition("refreshScopeJob");
            definition.setScope("refresh");
            ScopedProxyUtils.createScopedProxy(new BeanDefinitionHolder(definition, "refreshScopeJob"), registry, true);
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        }
    }

    @Configuration
    public static class ExecutorConfig {

        @Bean
        public XxlJobSpringExecutor xxlJobSpringExecutor() {
            XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
            // enabled=false：仅执行 Bean 扫描，跳过 start() 里的服务端/注册线程，便于离线验证
            executor.setEnabled(false);
            executor.setAdminAddresses("http://127.0.0.1:8080/xxl-job-admin");
            executor.setAppname("test-executor");
            executor.setAccessToken("default_token");
            executor.setPort(0);
            return executor;
        }

        @Bean
        public RefreshScopeJob refreshScopeJob() {
            return new RefreshScopeJob();
        }

        @Bean
        public PlainScopeJob plainScopeJob() {
            return new PlainScopeJob();
        }
    }

    public static class RefreshScopeJob {

        @XxlJob("refreshScopeJob")
        public void execute() {
        }
    }

    public static class PlainScopeJob {

        @XxlJob("plainScopeJob")
        public void execute() {
        }
    }
}
