package com.xxl.job.admin.core.model;

import java.util.Date;

/**
 * xxl-job operation log
 */
public class XxlJobOpLog {
	
	private long id;

	// log info
	private String logType;
	private String description;
	private String oldVal;
	private String newVal;
	private String createUser;
	private Date createTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOldVal() {
		return oldVal;
	}

	public void setOldVal(String oldVal) {
		this.oldVal = oldVal;
	}

	public String getNewVal() {
		return newVal;
	}

	public void setNewVal(String newVal) {
		this.newVal = newVal;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
