package com.xxl.job.executor.factory.handler;


import com.xxl.job.executor.context.XxlJobHelper;
import lombok.AllArgsConstructor;

import java.lang.reflect.Method;

/**
 * 方法处理程序
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@AllArgsConstructor
public class MethodJobHandler extends JobHandler {

    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;

    @Override
    public void execute(Object param) throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length > 0) {
            Object jobParam = XxlJobHelper.getJobParam(param, paramTypes[0]);
            Object[] obj = new Object[paramTypes.length];
            obj[0] = jobParam;
            // method-param can not be primitive-types
            method.invoke(target, obj);
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
