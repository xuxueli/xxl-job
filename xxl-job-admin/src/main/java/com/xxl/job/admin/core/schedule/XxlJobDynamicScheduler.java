package com.xxl.job.admin.core.schedule;

import com.xxl.job.admin.core.jobbean.RemoteHttpJobBean;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.thread.JobFailMonitorHelper;
import com.xxl.job.admin.core.thread.JobRegistryMonitorHelper;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.admin.dao.IXxlJobRegistryDao;
import com.xxl.job.core.rpc.netcom.NetComServerFactory;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;

/**
 * base quartz schedulerFactoryBean.getScheduler() util
 *
 * @author xuxueli 2015-12-19 16:13:53
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class XxlJobDynamicScheduler implements ApplicationListener<ApplicationEvent> {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobDynamicScheduler.class);

    private final JobFailMonitorHelper jobFailMonitorHelper;
    private final JobRegistryMonitorHelper jobRegistryMonitorHelper;

    // xxlJobLogDao、xxlJobInfoDao
    private final IXxlJobLogDao xxlJobLogDao;
    private final IXxlJobInfoDao xxlJobInfoDao;
    private final IXxlJobRegistryDao xxlJobRegistryDao;
    private final IXxlJobGroupDao xxlJobGroupDao;
    private final SchedulerFactoryBean schedulerFactoryBean;


    // init
    private NetComServerFactory serverFactory = new NetComServerFactory();



    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextClosedEvent) {
            destroy();
        }
    }


    @PostConstruct
    public void init() throws Exception {
        // admin registry monitor run
        jobRegistryMonitorHelper.start();

        // admin monitor run
        jobFailMonitorHelper.start();

        Assert.notNull(schedulerFactoryBean.getScheduler(), "quartz scheduler() is null");

        logger.info(">>>>>>>>> init quartz scheduler() success.[{}]", schedulerFactoryBean.getScheduler());
    }


    // destroy
    public void destroy() {
        // admin registry stop
        jobRegistryMonitorHelper.toStop();

        // admin monitor stop
        jobFailMonitorHelper.toStop();

        serverFactory.destroy();
    }


    // fill job info
    public void fillJobInfo(XxlJobInfo jobInfo) {
        // TriggerKey : name + group
        String group = String.valueOf(jobInfo.getJobGroup());
        String name = String.valueOf(jobInfo.getId());
        TriggerKey triggerKey = TriggerKey.triggerKey(name, group);

        try {
            Trigger trigger = schedulerFactoryBean.getScheduler().getTrigger(triggerKey);

            TriggerState triggerState = schedulerFactoryBean.getScheduler().getTriggerState(triggerKey);

            // parse params
            if (trigger != null && trigger instanceof CronTriggerImpl) {
                String cronExpression = ((CronTriggerImpl) trigger).getCronExpression();
                jobInfo.setJobCron(cronExpression);
            }

            //JobKey jobKey = new JobKey(jobInfo.getJobName(), String.valueOf(jobInfo.getJobGroup()));
            //JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
            //String jobClass = jobDetail.getJobClass().getName();

            if (triggerState != null) {
                jobInfo.setJobStatus(triggerState.name());
            }

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    // check if exists
    public boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        return schedulerFactoryBean.getScheduler().checkExists(triggerKey);
    }

    // addJob 新增
    @SuppressWarnings("unchecked")
    public boolean addJob(String jobName, String jobGroup, String cronExpression) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);

        // TriggerKey valid if_exists
        if (checkExists(jobName, jobGroup)) {
            logger.info(">>>>>>>>> addJob fail, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
            return false;
        }

        // CronTrigger : TriggerKey + cronExpression	// withMisfireHandlingInstructionDoNothing 忽略掉调度终止过程中忽略的调度
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

        // JobDetail : jobClass
        Class<? extends Job> jobClass_ = RemoteHttpJobBean.class;   // Class.forName(jobInfo.getJobClass());

        JobDetail jobDetail = JobBuilder.newJob(jobClass_).withIdentity(jobKey).build();
        /*if (jobInfo.getJobData()!=null) {
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
        	jobDataMap.putAll(JacksonUtil.readValue(jobInfo.getJobData(), Map.class));	
        	// JobExecutionContext context.getMergedJobDataMap().get("mailGuid");
		}*/

        // schedule : jobDetail + cronTrigger
        Date date = schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, cronTrigger);

        logger.info(">>>>>>>>>>> addJob success, jobDetail:{}, cronTrigger:{}, date:{}", jobDetail, cronTrigger, date);
        return true;
    }

    // reschedule
    public boolean rescheduleJob(String jobGroup, String jobName, String cronExpression) throws SchedulerException {

        // TriggerKey valid if_exists
        if (!checkExists(jobName, jobGroup)) {
            logger.info(">>>>>>>>>>> rescheduleJob fail, job not exists, JobGroup:{}, JobName:{}", jobGroup, jobName);
            return false;
        }

        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        CronTrigger oldTrigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(triggerKey);

        if (oldTrigger != null) {
            // avoid repeat
            String oldCron = oldTrigger.getCronExpression();
            if (oldCron.equals(cronExpression)) {
                return true;
            }

            // CronTrigger : TriggerKey + cronExpression
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            oldTrigger = oldTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

            // rescheduleJob
            schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, oldTrigger);
        } else {
            // CronTrigger : TriggerKey + cronExpression
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

            // JobDetail-JobDataMap fresh
            JobKey jobKey = new JobKey(jobName, jobGroup);
            JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
            /*JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.clear();
            jobDataMap.putAll(JacksonUtil.readValue(jobInfo.getJobData(), Map.class));*/

            // Trigger fresh
            HashSet<Trigger> triggerSet = new HashSet<Trigger>();
            triggerSet.add(cronTrigger);

            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, triggerSet, true);
        }

        logger.info(">>>>>>>>>>> resumeJob success, JobGroup:{}, JobName:{}", jobGroup, jobName);
        return true;
    }

    // unscheduleJob
    public boolean removeJob(String jobName, String jobGroup) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        boolean result = false;
        if (checkExists(jobName, jobGroup)) {
            result = schedulerFactoryBean.getScheduler().unscheduleJob(triggerKey);
            logger.info(">>>>>>>>>>> removeJob, triggerKey:{}, result [{}]", triggerKey, result);
        }
        return true;
    }

    // Pause
    public boolean pauseJob(String jobName, String jobGroup) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        boolean result = false;
        if (checkExists(jobName, jobGroup)) {
            schedulerFactoryBean.getScheduler().pauseTrigger(triggerKey);
            result = true;
            logger.info(">>>>>>>>>>> pauseJob success, triggerKey:{}", triggerKey);
        } else {
            logger.info(">>>>>>>>>>> pauseJob fail, triggerKey:{}", triggerKey);
        }
        return result;
    }

    // resume
    public boolean resumeJob(String jobName, String jobGroup) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        boolean result = false;
        if (checkExists(jobName, jobGroup)) {
            schedulerFactoryBean.getScheduler().resumeTrigger(triggerKey);
            result = true;
            logger.info(">>>>>>>>>>> resumeJob success, triggerKey:{}", triggerKey);
        } else {
            logger.info(">>>>>>>>>>> resumeJob fail, triggerKey:{}", triggerKey);
        }
        return result;
    }

    // run
    public boolean triggerJob(String jobName, String jobGroup) throws SchedulerException {
        // TriggerKey : name + group
        JobKey jobKey = new JobKey(jobName, jobGroup);

        boolean result = false;
        if (checkExists(jobName, jobGroup)) {
            schedulerFactoryBean.getScheduler().triggerJob(jobKey);
            result = true;
            logger.info(">>>>>>>>>>> runJob success, jobKey:{}", jobKey);
        } else {
            logger.info(">>>>>>>>>>> runJob fail, jobKey:{}", jobKey);
        }
        return result;
    }
}