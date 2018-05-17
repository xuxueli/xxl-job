package com.xxl.job.core.annotationtask.spring;


import com.xxl.job.core.annotationtask.model.ExecutorParam;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.executor.XxlJobExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class XxlJobFactory implements InvocationHandler {

    private static XxlJobFactory xxlJobFactory = new XxlJobFactory();

    public static <T> T newProxy(Class<T> interfaceType){
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{interfaceType},xxlJobFactory);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<AdminBiz> adminBizs =  XxlJobExecutor.getAdminBizList();
        ReturnT ret=null;
        for (AdminBiz adminBiz : adminBizs){
            String identity = method.getDeclaringClass().getCanonicalName()+"."+method.getName();
            ExecutorParam executorParam =null;
            if(args.length==1){
                 executorParam = (ExecutorParam) args[0];
                 executorParam.getParam().put(ExecutorParam.ANNOTATION_IDENTITY,identity);
                 ret = adminBiz.triggerAnnotationJob(executorParam);
                if(ret.getCode()==ReturnT.SUCCESS_CODE){
                    break;
                }
            }else{
                throw new RuntimeException("xxlJob proxy 参数必须为ExecutorParam");
            }
        }
        return ret;
    }
}
