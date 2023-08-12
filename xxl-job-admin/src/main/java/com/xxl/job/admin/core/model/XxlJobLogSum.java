package com.xxl.job.admin.core.model;

/**
 * xxl-job log, used to track trigger process
 * @author Xu Lei  2022-11-19 23:19:09
 */
public class XxlJobLogSum {
	private int jobId;
	private String appName;
	private String jobDesc;
	private String executorHandler;
	private int count;
	
	
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public String getExecutorHandler() {
		return executorHandler;
	}
	public void setExecutorHandler(String executorHandler) {
		this.executorHandler = executorHandler;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getJobDesc() {
		return jobDesc;
	}
	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	
}
