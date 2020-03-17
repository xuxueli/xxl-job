package com.xxl.job.core.executor.impl;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * xxl-job executor (for spring)
 *
 * @author xuxueli 2018-11-01 09:24:52
 */
public class XxlJobSpringExecutor extends XxlJobExecutor implements ApplicationContextAware, InitializingBean, DisposableBean, BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        registerClassJobIfNecessary(bean);
        registerMethodJobIfNecessary(bean);
        return bean;
    }

    private void registerMethodJobIfNecessary(Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method: methods) {
            XxlJob xxlJob = AnnotationUtils.findAnnotation(method, XxlJob.class);
            if (xxlJob == null) {
                continue;
            }

            // name
            String name = xxlJob.value();
            if (name.trim().length() == 0) {
                throw new RuntimeException("xxl-job method-jobhandler name invalid, for[" + bean.getClass() + "#"+ method.getName() +"] .");
            }
            if (loadJobHandler(name) != null) {
                throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
            }

            // execute method
            if (!(method.getParameterTypes()!=null && method.getParameterTypes().length==1 && method.getParameterTypes()[0].isAssignableFrom(String.class))) {
                throw new RuntimeException("xxl-job method-jobhandler param-classtype invalid, for[" + bean.getClass() + "#"+ method.getName() +"] , " +
                        "The correct method format like \" public ReturnT<String> execute(String param) \" .");
            }
            if (!method.getReturnType().isAssignableFrom(ReturnT.class)) {
                throw new RuntimeException("xxl-job method-jobhandler return-classtype invalid, for[" + bean.getClass() + "#"+ method.getName() +"] , " +
                        "The correct method format like \" public ReturnT<String> execute(String param) \" .");
            }
            method.setAccessible(true);

            // init and destory
            Method initMethod = null;
            Method destroyMethod = null;

            if(xxlJob.init().trim().length() > 0) {
                try {
                    initMethod = bean.getClass().getDeclaredMethod(xxlJob.init());
                    initMethod.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("xxl-job method-jobhandler initMethod invalid, for[" + bean.getClass() + "#"+ method.getName() +"] .");
                }
            }
            if(xxlJob.destroy().trim().length() > 0) {
                try {
                    destroyMethod = bean.getClass().getDeclaredMethod(xxlJob.destroy());
                    destroyMethod.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("xxl-job method-jobhandler destroyMethod invalid, for[" + bean.getClass() + "#"+ method.getName() +"] .");
                }
            }

            // registry jobhandler
            registJobHandler(name, new MethodJobHandler(bean, method, initMethod, destroyMethod));
        }
    }

    private void registerClassJobIfNecessary(Object bean) {
        if (!(bean instanceof IJobHandler)) {
            return;
        }
        if (!bean.getClass().isAnnotationPresent(JobHandler.class)) {
            return;
        }
        String name = bean.getClass().getAnnotation(JobHandler.class).value();
        IJobHandler handler = (IJobHandler) bean;
        if (loadJobHandler(name) != null) {
            throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
        }
        registJobHandler(name, handler);
    }

    // start
    @Override
    public void afterPropertiesSet() throws Exception {
        // refresh GlueFactory
        GlueFactory.refreshInstance(1);
        // super start
        super.start();
    }

    // destroy
    @Override
    public void destroy() {
        super.destroy();
    }


    // ---------------------- applicationContext ----------------------
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
