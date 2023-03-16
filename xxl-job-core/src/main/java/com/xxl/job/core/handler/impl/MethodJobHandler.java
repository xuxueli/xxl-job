package com.xxl.job.core.handler.impl;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.converter.Converter;
import com.xxl.job.core.handler.IJobHandler;

import java.lang.reflect.Method;

/**
 * @author xuxueli 2019-12-11 21:12:18
 */
public class MethodJobHandler extends IJobHandler {

    private final Object target;
    private final Method method;
    private final Converter<?> converter;
    private Method initMethod;
    private Method destroyMethod;

    public MethodJobHandler(Object target, Method method, Converter<?> converter, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.method = method;
        this.converter = converter;

        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    @Override
    public void execute() throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        String jobParam = XxlJobContext.getXxlJobContext().getJobParam();
        if (paramTypes.length > 0) {
            if (this.converter != null) {
                // method-param can not be primitive-types
                this.method.invoke(target, this.converter.convertTo(jobParam));
            } else {
                this.method.invoke(target, jobParam);
            }

        } else {
            method.invoke(target);
        }
    }

    @Override
    public void init() throws Exception {
        if(initMethod != null) {
            initMethod.invoke(target);
        }
    }

    @Override
    public void destroy() throws Exception {
        if(destroyMethod != null) {
            destroyMethod.invoke(target);
        }
    }

    @Override
    public String toString() {
        return super.toString()+"["+ target.getClass() + "#" + method.getName() +"]";
    }
}
