package com.xxl.job.admin.core.model;

import java.sql.Timestamp;

/**
 * 
 * @author Locki 2020-03-26
 *
 */
public class XxlAlarmInfo {
	private long id;
	private String alarmName;
	private String alarmType;
	private String alarmParam;
	private String alarmDesc;
	private Timestamp createTime;
	private Timestamp updateTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getAlarmParam() {
		return alarmParam;
	}

	public void setAlarmParam(String alarmParam) {
		this.alarmParam = alarmParam;
	}

	public String getAlarmDesc() {
		return alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "XxlAlarmInfo [id=" + id + ", alarmName=" + alarmName + ", alarmType=" + alarmType + ", alarmParam="
				+ alarmParam + ", alarmDesc=" + alarmDesc + ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}
}
