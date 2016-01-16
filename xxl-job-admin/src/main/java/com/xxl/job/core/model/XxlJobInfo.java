package com.xxl.job.core.model;

import java.util.Date;

/**
 * xxl-job info
 * @author xuxueli  2016-1-12 18:25:49
 */
public class XxlJobInfo {
	
	private int id;
	
	private String jobGroup;	// 任务组
	private String jobName;		// 任务名
	private String jobCron;		// 任务执行CRON表达式 【base on quartz】
	private String jobDesc;
	private String jobClass;	// 任务执行JobBean 【base on quartz】
	private String jobData;		// 任务执行数据 Map-JSON-String
	
	private Date addTime;
	private Date updateTime;
	
	private String author;		// 负责人
	private String alarmEmail;	// 报警邮件
	private int alarmThreshold;	// 报警阀值
	
	// copy from quartz
	private String jobStatus;	// 任务状态 【base on quartz】

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobCron() {
		return jobCron;
	}

	public void setJobCron(String jobCron) {
		this.jobCron = jobCron;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public String getJobData() {
		return jobData;
	}

	public void setJobData(String jobData) {
		this.jobData = jobData;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAlarmEmail() {
		return alarmEmail;
	}

	public void setAlarmEmail(String alarmEmail) {
		this.alarmEmail = alarmEmail;
	}

	public int getAlarmThreshold() {
		return alarmThreshold;
	}

	public void setAlarmThreshold(int alarmThreshold) {
		this.alarmThreshold = alarmThreshold;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	@Override
	public String toString() {
		return "XxlJobInfo [id=" + id + ", jobGroup=" + jobGroup + ", jobName=" + jobName + ", jobCron=" + jobCron
				+ ", jobDesc=" + jobDesc + ", jobClass=" + jobClass + ", jobData=" + jobData + ", addTime=" + addTime
				+ ", updateTime=" + updateTime + ", author=" + author + ", alarmEmail=" + alarmEmail
				+ ", alarmThreshold=" + alarmThreshold + ", jobStatus=" + jobStatus + "]";
	}

}
