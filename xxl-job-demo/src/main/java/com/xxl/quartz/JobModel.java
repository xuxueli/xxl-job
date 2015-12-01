package com.xxl.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

/**
 * 任务model
 * @author xuxueli 2015-12-1 16:01:19
 */
public class JobModel {
	
	// param
	private String group;
	private String name;
	private String cronExpression;
	private Class<? extends Job> jobClass;
	
    public JobModel(String name, String cronExpression, Class<? extends Job> jobClass) {
		this.group = Scheduler.DEFAULT_GROUP;
		this.name = name;
		this.cronExpression = cronExpression;
		this.jobClass = jobClass;
	}
    
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public Class<? extends Job> getJobClass() {
		return jobClass;
	}
	public void setJobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
	}
	
	// TriggerKey
	public TriggerKey getTriggerKey() {
		return TriggerKey.triggerKey(this.name, this.group);
	}
	// JobDetail
	public JobDetail getJobDetail() {
		return JobBuilder.newJob(jobClass).withIdentity(this.name, this.group).build();
	}
	// JobDataMap.add
    public JobModel addJobData(String key, Object value) {
        JobDataMap jobDataMap = this.getJobDetail().getJobDataMap();
        jobDataMap.put(key, value);
        return this;
    }
    // CronTrigger
    public CronTrigger cronTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(this.cronExpression);
        return TriggerBuilder.newTrigger().withIdentity(this.getTriggerKey()).withSchedule(cronScheduleBuilder).build();
    }

}