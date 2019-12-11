package com.xxl.job.core.handler.impl;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author liuzh 2019-12-07
 */
public class MethodJobHandler extends IJobHandler {
    private static Logger logger = LoggerFactory.getLogger(MethodJobHandler.class);

    private final Object target;
    private final Method method;
    private final JobHandler jobHandler;
    private Method initMethod;
    private Method destroyMethod;

    public MethodJobHandler(Object target, Method method, JobHandler jobHandler) {
        this.target = target;
        this.method = method;
        this.jobHandler = jobHandler;
        this.method.setAccessible(true);
        this.prepareMethod();
    }

    protected void prepareMethod() {
        String init = jobHandler.init();
        if(!init.isEmpty()) {
            try {
                initMethod = target.getClass().getDeclaredMethod(init);
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                logger.warn(e.getMessage(), e);
            }
        }
        String destroy = jobHandler.destroy();
        if(!destroy.isEmpty()) {
            try {
                destroyMethod = target.getClass().getDeclaredMethod(destroy);
                destroyMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        return (ReturnT<String>) method.invoke(target, new Object[]{param});
    }

    @Override
    public void init() {
        super.init();
        if(initMethod != null) {
            try {
                initMethod.invoke(target);
            } catch (IllegalAccessException e) {
                logger.warn(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if(destroyMethod != null) {
            try {
                destroyMethod.invoke(target);
            } catch (IllegalAccessException e) {
                logger.warn(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public JobHandler getJobHandler() {
        return jobHandler;
    }
}
