package com.xxl.job.admin.core.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * xxl-job log, used to track trigger process
 * @author xuxueli  2015-12-19 23:19:09
 */
@Entity
@Table(name = "xxl_job_log", indexes = {@Index(name = "I_trigger_time", columnList = "trigger_time"),
		@Index(name = "I_handle_code", columnList = "handle_code")})
public class XxlJobLog {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdentityGenerator") // 使用基于雪花算法的主键生成策略
	@GenericGenerator(name = "IdentityGenerator", strategy = "com.xxl.job.admin.core.util.XxlJobGenerator")
	private Long id;
	
	// job info
	@Column(name = "job_group", nullable = false, length = 20)
	private Long jobGroup;
	@Column(name = "job_id", nullable = false, length = 20)
	private Long jobId;

	// execute info
	@Column(name = "executor_address", length = 255)
	private String executorAddress;
	@Column(name = "executor_handler", length = 255)
	private String executorHandler;
	@Column(name = "executor_param", length = 512)
	private String executorParam;
	@Column(name = "executor_sharding_param", length = 20)
	private String executorShardingParam;
	@Column(name = "executor_fail_retry_count", nullable = false, length = 11)
	private int executorFailRetryCount;
	
	// trigger info
	@Column(name = "trigger_time")
	private Date triggerTime;
	@Column(name = "trigger_code", nullable = false, length = 11)
	private int triggerCode;
	@Column(name = "trigger_msg", columnDefinition = "text")
	private String triggerMsg;
	
	// handle info
	@Column(name = "handle_time")
	private Date handleTime;
	@Column(name = "handle_code", nullable = false, length = 11)
	private int handleCode;
	@Column(name = "handle_msg", columnDefinition = "text")
	private String handleMsg;

	// alarm info
	@Column(name = "alarm_status", nullable = false, length = 4)
	private int alarmStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(Long jobGroup) {
		this.jobGroup = jobGroup;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
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
