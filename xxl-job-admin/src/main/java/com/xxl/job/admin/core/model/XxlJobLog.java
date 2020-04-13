package com.xxl.job.admin.core.model;

import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Date;

/**
 * xxl-job log, used to track trigger process
 * @author xuxueli  2015-12-19 23:19:09
 */
@Entity
@Table(name = "job_log")
@Proxy(lazy=false)
@TableGenerator(name = "job_log_gen",
		table="primary_key_gen",
		pkColumnName="gen_name",
		valueColumnName="gen_value",
		pkColumnValue="JOB_LOG_PK",
		allocationSize=1
)
public class XxlJobLog {

	@Column(length = 20,nullable = false)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="job_log_gen")
	private Long id;
	
	// job info
	@Column(name = "job_group",length = 11,nullable = false)
	private int jobGroup;
	@Column(name = "job_id",length = 11,nullable = false)
	private int jobId;

	// execute info
	@Column(name = "executor_address")
	private String executorAddress;
	@Column(name = "executor_handler")
	private String executorHandler;
	@Column(name = "executor_param",length = 512)
	private String executorParam;
	@Column(name = "executor_sharding_param",length = 20)
	private String executorShardingParam;
	@Column(name = "executor_fail_retry_count",length = 11,nullable = false)
	private int executorFailRetryCount;
	
	// trigger info
	@Column(name = "trigger_time")
	private Date triggerTime;
	@Column(name = "trigger_code",nullable = false,length = 11)
	private int triggerCode;
	@Lob
	@Column(name = "trigger_msg")
	private String triggerMsg;
	
	// handle info
	@Column(name = "handle_time")
	private Date handleTime;
	@Column(name = "handle_code",length = 11,nullable = false)
	private int handleCode;
	@Lob
	@Column(name = "handle_msg")
	private String handleMsg;

	// alarm info
	@Column(name = "alarm_status",length = 4,nullable = false)
	private int alarmStatus;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(int jobGroup) {
		this.jobGroup = jobGroup;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public String getExecutorAddress() {
		return executorAddress;
	}

	public void setExecutorAddress(String executorAddress) {
		this.executorAddress = executorAddress;
	}

	public String getExecutorHandler() {
		return executorHandler;
	}

	public void setExecutorHandler(String executorHandler) {
		this.executorHandler = executorHandler;
	}

	public String getExecutorParam() {
		return executorParam;
	}

	public void setExecutorParam(String executorParam) {
		this.executorParam = executorParam;
	}

	public String getExecutorShardingParam() {
		return executorShardingParam;
	}

	public void setExecutorShardingParam(String executorShardingParam) {
		this.executorShardingParam = executorShardingParam;
	}

	public int getExecutorFailRetryCount() {
		return executorFailRetryCount;
	}

	public void setExecutorFailRetryCount(int executorFailRetryCount) {
		this.executorFailRetryCount = executorFailRetryCount;
	}

	public Date getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(Date triggerTime) {
		this.triggerTime = triggerTime;
	}

	public int getTriggerCode() {
		return triggerCode;
	}

	public void setTriggerCode(int triggerCode) {
		this.triggerCode = triggerCode;
	}

	public String getTriggerMsg() {
		return triggerMsg;
	}

	public void setTriggerMsg(String triggerMsg) {
		this.triggerMsg = triggerMsg;
	}

	public Date getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(Date handleTime) {
		this.handleTime = handleTime;
	}

	public int getHandleCode() {
		return handleCode;
	}

	public void setHandleCode(int handleCode) {
		this.handleCode = handleCode;
	}

	public String getHandleMsg() {
		return handleMsg;
	}

	public void setHandleMsg(String handleMsg) {
		this.handleMsg = handleMsg;
	}

	public int getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(int alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

}
