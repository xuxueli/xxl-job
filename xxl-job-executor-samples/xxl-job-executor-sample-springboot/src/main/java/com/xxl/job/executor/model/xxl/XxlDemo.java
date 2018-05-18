package com.xxl.job.executor.model.xxl;

import com.xxl.job.core.annotationtask.annotations.DestroyJob;
import com.xxl.job.core.annotationtask.annotations.Xxl;
import com.xxl.job.core.annotationtask.annotations.XxlJob;
import com.xxl.job.core.annotationtask.model.ExecutorParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.executor.service.jobhandler.DemoJobHandler;

import java.util.concurrent.Executor;


/**
 * 注意参数只能是ExecutorParam
 */
@Xxl
public interface XxlDemo {

    @XxlJob(jobCron = "0 */1 * * * ?",jobDesc = "demo01",
            author = "mrchenli",alarmEmail = "1278530889@qq.com",
            executorHandler = DemoJobHandler.class,executorParam = "demo01",onStart = true)
    ReturnT<String> demo01(ExecutorParam executorParam);

    @XxlJob(jobCron = "0 */1 * * * ?",jobDesc = "demo02",
            author = "mrchenli",alarmEmail = "1278530889@qq.com",
            executorHandler = DemoJobHandler.class,executorParam = "demo02",onStart = true)
    @DestroyJob
    ReturnT<String> demo02(ExecutorParam executorParam);

    @XxlJob(jobCron = "0 */1 * * * ?",jobDesc = "demo03",
            author = "mrchenli",alarmEmail = "1278530889@qq.com",
            executorHandler = DemoJobHandler.class,executorParam = "demo03")
    ReturnT<String> demo03(ExecutorParam executorParam);

}
