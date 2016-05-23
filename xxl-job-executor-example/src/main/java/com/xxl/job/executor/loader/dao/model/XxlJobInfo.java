package com.xxl.job.executor.loader.dao.model;

/**
 * xxl-job info
 * @author xuxueli 2016-5-19 17:57:46
 */
public class XxlJobInfo {
	
	private String jobGroup;
	private String jobName;
	
	private String glueSource;

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

	public String getGlueSource() {
		return glueSource;
	}

	public void setGlueSource(String glueSource) {
		this.glueSource = glueSource;
	}
	
}
