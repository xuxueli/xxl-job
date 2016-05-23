package com.xxl.job.admin.core.model;

/**
 * xxl-job log for glue, used to track job code process
 * @author xuxueli 2016-5-19 17:57:46
 */
public class XxlJobLogGlue {
	
	private int id;
	
	private String jobGroup;
	private String jobName;
	
	private String glueSource;
	private String glueRemark;
	private String addTime;
	private String updateTime;
	
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
	public String getGlueSource() {
		return glueSource;
	}
	public void setGlueSource(String glueSource) {
		this.glueSource = glueSource;
	}
	public String getGlueRemark() {
		return glueRemark;
	}
	public void setGlueRemark(String glueRemark) {
		this.glueRemark = glueRemark;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	@Override
	public String toString() {
		return "XxlJobLogGlue [id=" + id + ", jobGroup=" + jobGroup + ", jobName=" + jobName + ", glueSource="
				+ glueSource + ", glueRemark=" + glueRemark + ", addTime=" + addTime + ", updateTime=" + updateTime
				+ "]";
	}
	
}
