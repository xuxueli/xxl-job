package com.xxl.job.admin.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

/**
 * xxl-job log, used to track trigger process
 * @author xuxueli  2015-12-19 23:19:09
 */
@Entity
@Table(name = "xxl_job_log", indexes = { @Index(name = "I_trigger_time", columnList = "trigger_time"),
		@Index(name = "I_handle_code", columnList = "handle_code") })
public class XxlJobLog {
	
	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "native", parameters = {
			@org.hibernate.annotations.Parameter(name = "sequence_name", value = "s_xxl_job_log") })
	@Column(name = "id", nullable = false, unique = true)
	private long id;
	
	// job info
	@Column(name = "job_group", nullable = false)
	@Comment("执行器主键ID")
	private int jobGroup;
	@Column(name = "job_id", nullable = false)
	@Comment("任务，主键ID")
	private int jobId;

	// execute info
	@Column(name = "executor_address", length = 255)
	@Comment("执行器地址，本次执行的地址")
	private String executorAddress;
	@Column(name = "executor_handler", length = 255)
	@Comment("执行器任务handler")
	private String executorHandler;
	@Column(name = "executor_param", length = 512)
	@Comment("执行器任务参数")
	private String executorParam;
	@Column(name = "executor_sharding_param", length = 20)
	@Comment("执行器任务分片参数，格式如 1/2")
	private String executorShardingParam;
	@Column(name = "executor_fail_retry_count", nullable = false)
	@ColumnDefault("0")
	@Comment("失败重试次数")
	private int executorFailRetryCount;
	
	// trigger info
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "trigger_time")
	@Comment("调度-时间")
	private Date triggerTime;
	@Column(name = "trigger_code", nullable = false)
	@Comment("调度-结果")
	private int triggerCode;
	@Lob
	@Column(name = "trigger_msg", length = 16*1024)
	@Comment("调度-日志")
	private String triggerMsg;
	
	// handle info
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "handle_time")
	@Comment("执行-时间")
	private Date handleTime;
	@Column(name = "handle_code", nullable = false)
	@Comment("执行-状态")
	private int handleCode;
	@Lob
	@Column(name = "handle_msg", length = 16*1024)
	@Comment("执行-日志")
	private String handleMsg;

	// alarm info
	@Column(name = "alarm_status", nullable = false)
	@ColumnDefault("0")
	@Comment("告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败")
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
