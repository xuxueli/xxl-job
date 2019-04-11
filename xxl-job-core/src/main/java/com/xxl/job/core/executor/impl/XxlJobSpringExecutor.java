package com.xxl.job.core.executor.impl;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.AbstractJobHandler;
import com.xxl.job.core.handler.AbstractMultiJobHandler;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.handler.annotation.JobMethod;
import com.xxl.job.core.handler.annotation.MultiplexJobHandler;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * xxl-job executor (for spring)
 *
 * @author xuxueli 2018-11-01 09:24:52
 * modified by xmc
 */
public class XxlJobSpringExecutor extends XxlJobExecutor implements ApplicationContextAware {


    @Override
    public void start() throws Exception {

        // init JobHandler Repository
        initJobHandlerRepository(applicationContext);

        // refresh GlueFactory
        GlueFactory.refreshInstance(1);


        // super start
        super.start();
    }

    private void initJobHandlerRepository(ApplicationContext applicationContext){
        if (applicationContext == null) {
            return;
        }

        // init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHandler.class);

        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof AbstractJobHandler){
                    String name = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                    AbstractJobHandler handler = (AbstractJobHandler) serviceBean;
                    if (loadJobHandler(name) != null) {
                        throw new RuntimeException("xxl-job jobhandler naming conflicts.");
                    }
                    registJobHandler(name, handler);
                }
            }
        }
        
        // init job method(支持将多个job放入一个类中)
        Map<String, Object> multiplexJobBeanMap = applicationContext.getBeansWithAnnotation(MultiplexJobHandler.class);
        if(multiplexJobBeanMap != null && !multiplexJobBeanMap.isEmpty()){
        	for (Object multiplexJobBean : multiplexJobBeanMap.values()) {
        	   if (multiplexJobBean instanceof AbstractMultiJobHandler){
                   String name = multiplexJobBean.getClass().getAnnotation(MultiplexJobHandler.class).value();
                   if (loadJobHandler(name) != null) {
                       throw new RuntimeException("xxl-job jobhandler naming conflicts.");
                   }
                   AbstractMultiJobHandler multiplexHandler = (AbstractMultiJobHandler)multiplexJobBean;
                   registJobHandler(name, multiplexHandler);
                   
                   Method[] methods = multiplexJobBean.getClass().getDeclaredMethods();
                   for(Method method : methods){
                	   if(method.isAnnotationPresent(JobMethod.class)){
                		   JobMethod jm = method.getAnnotation(JobMethod.class);
                		   String jobMethodValue = jm.value();
                		   if (loadJobMethod(jobMethodValue) != null) {
                               throw new RuntimeException("xxl-job jobmethod naming conflicts.");
                           }
                		   registJobMethod(jobMethodValue, method);
                	   }
                   }
               }
        	}
        }
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
