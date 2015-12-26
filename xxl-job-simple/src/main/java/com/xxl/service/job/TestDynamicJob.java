package com.xxl.service.job;

import java.util.concurrent.TimeUnit;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.quartz.DynamicSchedulerUtil;

public class TestDynamicJob implements Job {
	private static Logger logger = LoggerFactory.getLogger(TestDynamicJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("xxl-job run: name:{}, group:{}, job_desc:{}",
        		new Object[]{context.getTrigger().getKey().getName(), context.getTrigger().getKey().getGroup(),
        		context.getMergedJobDataMap().get(DynamicSchedulerUtil.job_desc)});
        
        try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}