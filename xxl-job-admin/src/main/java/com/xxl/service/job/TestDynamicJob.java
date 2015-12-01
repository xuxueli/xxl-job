package com.xxl.service.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class TestDynamicJob implements Job {


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Object mailGuid = context.getMergedJobDataMap().get("mailGuid");
        System.out.println("[demo-job]  run at " + new Date() + " now, mailGuid=" + mailGuid);
    }
}