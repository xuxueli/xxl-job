package com.xxl.job.core.model;

import java.util.Date;

/**
 * xxl-job log, used to track trigger process
 * @author xuxueli  2015-12-19 23:19:09
 */
public class XxlJobLog {
	
	private String jobTriggerUuid;
	private String jobHandleName;
	// trigger info
	private Date triggerTime;
	private String triggerStatus;
	private String triggerDetailLog;
	// handle info
	private Date handleTime;
	private String handleStatus;
	private String handleDetailLog;
	
	public String getJobTriggerUuid() {
		return jobTriggerUuid;
	}
	public void setJobTriggerUuid(String jobTriggerUuid) {
		this.jobTriggerUuid = jobTriggerUuid;
	}
	public String getJobHandleName() {
		return jobHandleName;
	}
	public void setJobHandleName(String jobHandleName) {
		this.jobHandleName = jobHandleName;
	}
	public Date getTriggerTime() {
		return triggerTime;
	}
	public void setTriggerTime(Date triggerTime) {
		this.triggerTime = triggerTime;
	}
	public String getTriggerStatus() {
		return triggerStatus;
	}
	public void setTriggerStatus(String triggerStatus) {
		this.triggerStatus = triggerStatus;
	}
	public String getTriggerDetailLog() {
		return triggerDetailLog;
	}
	public void setTriggerDetailLog(String triggerDetailLog) {
		this.triggerDetailLog = triggerDetailLog;
	}
	public Date getHandleTime() {
		return handleTime;
	}
	public void setHandleTime(Date handleTime) {
		this.handleTime = handleTime;
	}
	public String getHandleStatus() {
		return handleStatus;
	}
	public void setHandleStatus(String handleStatus) {
		this.handleStatus = handleStatus;
	}
	public String getHandleDetailLog() {
		return handleDetailLog;
	}
	public void setHandleDetailLog(String handleDetailLog) {
		this.handleDetailLog = handleDetailLog;
	}
	
	@Override
	public String toString() {
		return "XxlJobLog [jobTriggerUuid=" + jobTriggerUuid + ", jobHandleName=" + jobHandleName
				+ ", triggerTime=" + triggerTime + ", triggerStatus=" + triggerStatus + ", triggerDetailLog="
				+ triggerDetailLog + ", handleTime=" + handleTime + ", handleStatus=" + handleStatus
				+ ", handleDetailLog=" + handleDetailLog + "]";
	}
	
}
