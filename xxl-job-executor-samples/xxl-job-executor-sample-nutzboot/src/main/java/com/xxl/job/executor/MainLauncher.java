package com.xxl.job.executor;

import org.nutz.boot.NbApp;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

/**
 * 使用NutzBoot作为xxl job executor的demo,简单修改后也可用于NutzMvc项目.
 * <p/>
 * 如有任何疑问,请访问 https://nutz.cn
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
@IocBean(create = "init")
public class MainLauncher {

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @At("/")
    @Ok("raw")
    public String index() {
        // demo嘛, 简单做个入口方法,显示一句话就够了
        return "xxl job executor running.";
    }

    // 如果是普通Nutz MVC项目,这段代码放到MainSetup.init方法内就可以了
    public void init() {
        // 从ioc容器中找出所有实现了IJobHandler接口的对象,注册到XxlJobExecutor
        for (String jobHandlerBeanName : ioc.getNamesByType(IJobHandler.class)) {
            // 获取JobHandler实例
            IJobHandler jobHandler = ioc.get(IJobHandler.class, jobHandlerBeanName);
            // 看看有没有@JobHandler注解
            JobHandler annoJobHandler = jobHandler.getClass().getAnnotation(JobHandler.class);
            // 得到jobHandlerName
            String jobHandlerName = annoJobHandler == null ? jobHandlerBeanName : annoJobHandler.value();
            // 注册到XxlJobExecutor上下文
            XxlJobExecutor.registJobHandler(jobHandlerName, jobHandler);
        }
        // 获取XxlJobExecutor,从而触发XxlJobExecutor的初始化
        ioc.getByType(XxlJobExecutor.class);
    }

    public static void main(String[] args) {
        new NbApp().run(); // 启动一切
    }

}