package com.xxl.job.core.executor.impl;

import com.xxl.job.core.handler.IJobHandler;

import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.Method;

/**
 * lazy init bean job handler
 *
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 * @since 2022/5/22 10:55
 */
final class SpringMethodJobHandler extends IJobHandler {

    private final String beanName;
    private final Class<?> handlerClass;
    private final BeanFactory beanFactory;

    private final Method jobMethod;
    private final Method initMethod;
    private final Method destroyMethod;

    SpringMethodJobHandler(String beanName, Class<?> handlerClass, BeanFactory beanFactory,
            Method jobMethod, Method initMethod, Method destroyMethod) {
        this.beanName = beanName;
        this.handlerClass = handlerClass;
        this.beanFactory = beanFactory;
        this.jobMethod = jobMethod;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    @Override
    public void execute() throws Exception {
        Object target = getTarget();
        Class<?>[] paramTypes = jobMethod.getParameterTypes();
        if (paramTypes.length > 0) {
            jobMethod.invoke(target, new Object[paramTypes.length]);       // method-param can not be primitive-types
        }
        else {
            jobMethod.invoke(target);
        }
    }

    @Override
    public void init() throws Exception {
        if (initMethod != null) {
            Object target = getTarget();
            initMethod.invoke(target);
        }
    }

    private Object getTarget() {
        return beanFactory.getBean(beanName);
    }

    @Override
    public void destroy() throws Exception {
        if (destroyMethod != null) {
            Object target = getTarget();
            destroyMethod.invoke(target);
        }
    }

    @Override
    public String toString() {
        return super.toString() + "[" + handlerClass + "#" + jobMethod.getName() + "]";
    }

}
