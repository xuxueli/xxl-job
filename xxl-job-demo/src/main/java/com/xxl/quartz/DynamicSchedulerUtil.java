package com.xxl.quartz;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Date;

public final class DynamicSchedulerUtil implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DynamicSchedulerUtil.class);
    
    // Scheduler
    private static Scheduler scheduler;
    public static void setScheduler(Scheduler scheduler) {
		DynamicSchedulerUtil.scheduler = scheduler;
	}

	@Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(scheduler, "quartz scheduler is null");
        logger.info(">>>>>>>>> init quartz scheduler success.[{}]", scheduler);
    }

	// Add 新增
    public static boolean addJob(JobModel job) throws SchedulerException {
        final TriggerKey triggerKey = job.getTriggerKey();
        if (scheduler.checkExists(triggerKey)) {
            final Trigger trigger = scheduler.getTrigger(triggerKey);
            logger.info(">>>>>>>>> Already exist trigger [" + trigger + "] by key [" + triggerKey + "] in Scheduler");
            return false;
        }

        final CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        final CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                .withSchedule(cronScheduleBuilder)
                .build();

        final JobDetail jobDetail = job.getJobDetail();
        final Date date = scheduler.scheduleJob(jobDetail, cronTrigger);

        logger.debug("Register DynamicJob {} on [{}]", job, date);
        return true;
    }

    // Pause 暂停-指定Job
    public static boolean pauseJob(JobModel existJob) throws SchedulerException {
        final TriggerKey triggerKey = existJob.getTriggerKey();
        boolean result = false;
        if (scheduler.checkExists(triggerKey)) {
            scheduler.pauseTrigger(triggerKey);
            result = true;
            logger.debug("Pause exist DynamicJob {}, triggerKey [{}] successful", existJob, triggerKey);
        } else {
            logger.debug("Failed pause exist DynamicJob {}, because not fount triggerKey [{}]", existJob, triggerKey);
        }
        return result;
    }

    // Resume 重启-指定Job
    public static boolean resumeJob(JobModel existJob) throws SchedulerException {
        final TriggerKey triggerKey = existJob.getTriggerKey();
        boolean result = false;
        if (scheduler.checkExists(triggerKey)) {
            final CronTrigger newTrigger = existJob.cronTrigger();
            final Date date = scheduler.rescheduleJob(triggerKey, newTrigger);

            result = true;
            logger.debug("Resume exist DynamicJob {}, triggerKey [{}] on [{}] successful", existJob, triggerKey, date);
        } else {
            logger.debug("Failed resume exist DynamicJob {}, because not fount triggerKey [{}]", existJob, triggerKey);
        }
        return result;
    }

    // Remove exists job 移除-指定Job
    public static boolean removeJob(JobModel existJob) throws SchedulerException {
        final TriggerKey triggerKey = existJob.getTriggerKey();
        boolean result = false;
        if (scheduler.checkExists(triggerKey)) {
            result = scheduler.unscheduleJob(triggerKey);
        }

        logger.debug("Remove DynamicJob {} result [{}]", existJob, result);
        return result;
    }


}