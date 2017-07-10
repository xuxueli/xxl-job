package com.xxl.job.spring.boot.autoconfigure.service.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;

import java.util.concurrent.TimeUnit;

/**
 * Author: Antergone
 * Date: 2017/7/7
 */
//@Component
@JobHander(value="springjob")
public class TestSpringHandler extends ISpringJobHandler {
    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        XxlJobLogger.log("XXL-JOB, Spring.");

        for (int i = 0; i < 5; i++) {
            XxlJobLogger.log("Spring beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
        return ReturnT.SUCCESS;
    }
}
