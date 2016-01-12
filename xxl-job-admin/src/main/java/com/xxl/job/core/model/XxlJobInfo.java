package com.xxl.job.core.model;

import java.util.Date;

/**
 * xxl-job info
 * @author xuxueli  2016-1-12 18:25:49
 */
public class XxlJobInfo {
	
	private int id;
	// job info
	private String jobName;
	private String jobCron;		// base on quartz
	private String jobClass;	// base on quartz
	private String jobStatus;	// base on quartz
	private String jobData;		// base on db, Map-JSON-String
	private Date addTime;
	private Date updateTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getJobClass() {
		return jobClass;
	}
	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
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
	
	@Override
	public String toString() {
		return "XxlJobInfo [id=" + id + ", jobName=" + jobName + ", jobCron=" + jobCron + ", jobClass=" + jobClass
				+ ", jobStatus=" + jobStatus + ", jobData=" + jobData + ", addTime=" + addTime + ", updateTime="
				+ updateTime + "]";
	}
	
}
