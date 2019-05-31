package com.xxl.job.executor;

import com.xuxueli.job.client.ExecutorRouteStrategyEnum;
import com.xuxueli.job.client.XxlJobClient;
import com.xuxueli.job.client.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Luo Bao Ding
 * @since 2019/5/27
 */
@Component
public class JobOpsDemoRunner implements ApplicationRunner {
    public static final String UNIQ_NAME = "auto_created_job";
    private final XxlJobClient xxlJobClient;

    public JobOpsDemoRunner(XxlJobClient xxlJobClient) {
        this.xxlJobClient = xxlJobClient;
    }


    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        Thread.sleep(3000);
        XxlJobInfo jobInfo;
        ReturnT<String> returnT;
        jobInfo = buildJob();

        //remove
        returnT = xxlJobClient.remove(UNIQ_NAME);
        Assert.isTrue(returnT.getCode() == 200, "update a job should succeed");

        //add
        returnT = xxlJobClient.add(jobInfo);
        Assert.isTrue(returnT.getCode() == 200 || returnT.getCode() == 1000, "add a job should succeed");

        //update
        jobInfo.setAuthor("tester-updated");
        returnT = xxlJobClient.update(jobInfo);
        Assert.isTrue(returnT.getCode() == 200, "update a job should succeed");

        //trigger by unique name
        returnT = xxlJobClient.trigger(UNIQ_NAME, "");
        Assert.isTrue(returnT.getCode() == 200, "update a job should succeed");

        //start
        returnT = xxlJobClient.start(UNIQ_NAME);
        Assert.isTrue(returnT.getCode() == 200, "update a job should succeed");

        Thread.sleep(3000);

        //stop
        returnT = xxlJobClient.stop(UNIQ_NAME);
        Assert.isTrue(returnT.getCode() == 200, "update a job should succeed");

    }

    private XxlJobInfo buildJob() {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setUniqName(UNIQ_NAME);
        jobInfo.setAppName("xxl-job-executor-sample");
        jobInfo.setJobCron("0/1 * * * * ? *");
        jobInfo.setJobDesc("test-job-ops");
        jobInfo.setExecutorHandler("DemoSimpleJobHandler");
        jobInfo.setAuthor("tester");
        jobInfo.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.FIRST.getName());
        jobInfo.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.COVER_EARLY.name());
        jobInfo.setGlueType(GlueTypeEnum.BEAN.name());
        return jobInfo;
    }
}
