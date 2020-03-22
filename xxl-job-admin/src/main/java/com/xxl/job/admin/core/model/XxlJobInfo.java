package com.xxl.job.admin.core.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
@Entity
@Table(name = "xxl_job_info")
public class XxlJobInfo {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdentityGenerator") // 使用基于雪花算法的主键生成策略
	@GenericGenerator(name = "IdentityGenerator", strategy = "com.xxl.job.admin.core.util.XxlJobGenerator")
	private Long id;				// 主键ID

	@Column(name = "job_group", nullable = false, length = 20)
	private Long jobGroup;		// 执行器主键ID
	@Column(name = "job_cron", nullable = false, length = 128)
	private String jobCron;		// 任务执行CRON表达式
	@Column(name = "job_desc", nullable = false, length = 255)
	private String jobDesc;

	@Column(name = "add_time")
	private Date addTime;
	@Column(name = "update_time")
	private Date updateTime;

	@Column(name = "author", length = 64)
	private String author;		// 负责人
	@Column(name = "alarm_email", length = 255)
	private String alarmEmail;	// 报警邮件

	@Column(name = "executor_route_strategy", length = 50)
	private String executorRouteStrategy;	// 执行器路由策略
	@Column(name = "executor_handler", length = 255)
	private String executorHandler;		    // 执行器，任务Handler名称
	@Column(name = "executor_param", length = 512)
	private String executorParam;		    // 执行器，任务参数
	@Column(name = "executor_block_strategy", length = 50)
	private String executorBlockStrategy;	// 阻塞处理策略
	@Column(name = "executor_timeout", nullable = false, length = 11)
	private int executorTimeout;     		// 任务执行超时时间，单位秒
	@Column(name = "executor_fail_retry_count", nullable = false, length = 11)
	private int executorFailRetryCount;		// 失败重试次数

	@Column(name = "glue_type", nullable = false, length = 50)
	private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	@Column(name = "glue_source", columnDefinition = "text")
	private String glueSource;		// GLUE源代码
	@Column(name = "glue_remark", length = 128)
	private String glueRemark;		// GLUE备注
	@Column(name = "glue_updatetime")
	private Date glueUpdatetime;	// GLUE更新时间

	@Column(name = "child_jobid", length = 255)
	private String childJobId;		// 子任务ID，多个逗号分隔

	@Column(name = "trigger_status", nullable = false, length = 4)
	private int triggerStatus;		// 调度状态：0-停止，1-运行
	@Column(name = "trigger_last_time", nullable = false, length = 13)
	private long triggerLastTime;	// 上次调度时间
	@Column(name = "trigger_next_time", nullable = false, length = 13)
	private long triggerNextTime;	// 下次调度时间


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

	public String getJobCron() {
		return jobCron;
	}

	public void setJobCron(String jobCron) {
		this.jobCron = jobCron;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAlarmEmail() {
		return alarmEmail;
	}

	public void setAlarmEmail(String alarmEmail) {
		this.alarmEmail = alarmEmail;
	}

	public String getExecutorRouteStrategy() {
		return executorRouteStrategy;
	}

	public void setExecutorRouteStrategy(String executorRouteStrategy) {
		this.executorRouteStrategy = executorRouteStrategy;
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

	public String getExecutorBlockStrategy() {
		return executorBlockStrategy;
	}

	public void setExecutorBlockStrategy(String executorBlockStrategy) {
		this.executorBlockStrategy = executorBlockStrategy;
	}

	public int getExecutorTimeout() {
		return executorTimeout;
	}

	public void setExecutorTimeout(int executorTimeout) {
		this.executorTimeout = executorTimeout;
	}

	public int getExecutorFailRetryCount() {
		return executorFailRetryCount;
	}

	public void setExecutorFailRetryCount(int executorFailRetryCount) {
		this.executorFailRetryCount = executorFailRetryCount;
	}

	public String getGlueType() {
		return glueType;
	}

	public void setGlueType(String glueType) {
		this.glueType = glueType;
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

	public Date getGlueUpdatetime() {
		return glueUpdatetime;
	}

	public void setGlueUpdatetime(Date glueUpdatetime) {
		this.glueUpdatetime = glueUpdatetime;
	}

	public String getChildJobId() {
		return childJobId;
	}

	public void setChildJobId(String childJobId) {
		this.childJobId = childJobId;
	}

	public int getTriggerStatus() {
		return triggerStatus;
	}

	public void setTriggerStatus(int triggerStatus) {
		this.triggerStatus = triggerStatus;
	}

	public long getTriggerLastTime() {
		return triggerLastTime;
	}

	public void setTriggerLastTime(long triggerLastTime) {
		this.triggerLastTime = triggerLastTime;
	}

	public long getTriggerNextTime() {
		return triggerNextTime;
	}

	public void setTriggerNextTime(long triggerNextTime) {
		this.triggerNextTime = triggerNextTime;
	}
}
