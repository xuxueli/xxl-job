package com.xxl.job.executor.model.xxl;

import com.xxl.job.core.annotationtask.annotations.Xxl;
import com.xxl.job.core.annotationtask.annotations.XxlJob;
import com.xxl.job.core.annotationtask.model.ExecutorParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.executor.service.jobhandler.DemoJobHandler;

import java.util.concurrent.Executor;


/**
 * 注意只支持一个Object参数
 */
@Xxl
public interface XxlDemo {

    @XxlJob(jobCron = "0 */1 * * * ?",jobDesc = "demo01",
            author = "mrchenli",alarmEmail = "1278530889@qq.com",
            executorHandler = DemoJobHandler.class,executorParam = "demo01")
    ReturnT<String> triggerDemo01(ExecutorParam executorParam);

    @XxlJob(jobCron = "0 */1 * * * ?",jobDesc = "demo02",
            author = "mrchenli",alarmEmail = "1278530889@qq.com",
            executorHandler = DemoJobHandler.class,executorParam = "demo02")
    ReturnT<String> triggerDemo02(ExecutorParam executorParam);

    @XxlJob(jobCron = "0 */1 * * * ?",jobDesc = "demo03",
            author = "mrchenli",alarmEmail = "1278530889@qq.com",
            executorHandler = DemoJobHandler.class,executorParam = "demo03")
    ReturnT<String> triggerDemo03(ExecutorParam executorParam);

}
