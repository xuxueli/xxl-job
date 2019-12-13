package com.xxl.job.admin.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * xxl-job info
 *
 * @author xuxueli 2016-1-12 18:25:49
 */
public class XxlJobInfo implements Serializable {
	private static final long serialVersionUID = -3312344419261421020L;

	private int id;				// 主键ID

	private int jobGroup;			// 执行器主键ID
	private String jobCron;			// 任务执行CRON表达式
	private String jobDesc;

	private String appName;			// 执行器AppName

	private Date addTime;
	private Date updateTime;

	private String author;			// 负责人
	private String alarmEmail;		// 报警邮件

	private String executorRouteStrategy;	// 执行器路由策略
	private String executorHandler;			// 执行器，任务Handler名称
	private String executorParam;			// 执行器，任务参数
	private String executorBlockStrategy;	// 阻塞处理策略
	private int executorTimeout;			// 任务执行超时时间，单位秒
	private int executorFailRetryCount;	// 失败重试次数

	private String glueType;		// GLUE类型 #com.xxl.job.core.glue.GlueTypeEnum
	private String glueSource;		// GLUE源代码
	private String glueRemark;		// GLUE备注
	private Date glueUpdatetime;	// GLUE更新时间

	private String childJobId;		// 子任务ID，多个逗号分隔

	private int triggerStatus;		// 调度状态：0-停止，1-运行
	private long triggerLastTime;	// 上次调度时间
	private long triggerNextTime;	// 下次调度时间
	private boolean leastOnce;		// 至少执行一次

	public int getId() {
		return id;
	}
	public XxlJobInfo setId(int id) {
		this.id = id;
		return this;
	}
	public int getJobGroup() {
		return jobGroup;
	}
	public XxlJobInfo setJobGroup(int jobGroup) {
		this.jobGroup = jobGroup;
		return this;
	}
	public String getJobCron() {
		return jobCron;
	}
	public XxlJobInfo setJobCron(String jobCron) {
		this.jobCron = jobCron;
		return this;
	}
	public String getJobDesc() {
		return jobDesc;
	}
	public XxlJobInfo setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
		return this;
	}
	public String getAppName() {
		return appName;
	}
	public XxlJobInfo setAppName(String appName) {
		this.appName = appName;
		return this;
	}
	public Date getAddTime() {
		return addTime;
	}
	public XxlJobInfo setAddTime(Date addTime) {
		this.addTime = addTime;
		return this;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public XxlJobInfo setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
		return this;
	}
	public String getAuthor() {
		return author;
	}
	public XxlJobInfo setAuthor(String author) {
		this.author = author;
		return this;
	}
	public String getAlarmEmail() {
		return alarmEmail;
	}
	public XxlJobInfo setAlarmEmail(String alarmEmail) {
		this.alarmEmail = alarmEmail;
		return this;
	}
	public String getExecutorRouteStrategy() {
		return executorRouteStrategy;
	}
	public XxlJobInfo setExecutorRouteStrategy(String executorRouteStrategy) {
		this.executorRouteStrategy = executorRouteStrategy;
		return this;
	}
	public String getExecutorHandler() {
		return executorHandler;
	}
	public XxlJobInfo setExecutorHandler(String executorHandler) {
		this.executorHandler = executorHandler;
		return this;
	}
	public String getExecutorParam() {
		return executorParam;
	}
	public XxlJobInfo setExecutorParam(String executorParam) {
		this.executorParam = executorParam;
		return this;
	}
	public String getExecutorBlockStrategy() {
		return executorBlockStrategy;
	}
	public XxlJobInfo setExecutorBlockStrategy(String executorBlockStrategy) {
		this.executorBlockStrategy = executorBlockStrategy;
		return this;
	}
	public int getExecutorTimeout() {
		return executorTimeout;
	}
	public XxlJobInfo setExecutorTimeout(int executorTimeout) {
		this.executorTimeout = executorTimeout;
		return this;
	}
	public int getExecutorFailRetryCount() {
		return executorFailRetryCount;
	}
	public XxlJobInfo setExecutorFailRetryCount(int executorFailRetryCount) {
		this.executorFailRetryCount = executorFailRetryCount;
		return this;
	}
	public String getGlueType() {
		return glueType;
	}
	public XxlJobInfo setGlueType(String glueType) {
		this.glueType = glueType;
		return this;
	}
	public String getGlueSource() {
		return glueSource;
	}
	public XxlJobInfo setGlueSource(String glueSource) {
		this.glueSource = glueSource;
		return this;
	}
	public String getGlueRemark() {
		return glueRemark;
	}
	public XxlJobInfo setGlueRemark(String glueRemark) {
		this.glueRemark = glueRemark;
		return this;
	}
	public Date getGlueUpdatetime() {
		return glueUpdatetime;
	}
	public XxlJobInfo setGlueUpdatetime(Date glueUpdatetime) {
		this.glueUpdatetime = glueUpdatetime;
		return this;
	}
	public String getChildJobId() {
		return childJobId;
	}
	public XxlJobInfo setChildJobId(String childJobId) {
		this.childJobId = childJobId;
		return this;
	}
	public int getTriggerStatus() {
		return triggerStatus;
	}
	public XxlJobInfo setTriggerStatus(int triggerStatus) {
		this.triggerStatus = triggerStatus;
		return this;
	}
	public long getTriggerLastTime() {
		return triggerLastTime;
	}
	public XxlJobInfo setTriggerLastTime(long triggerLastTime) {
		this.triggerLastTime = triggerLastTime;
		return this;
	}
	public long getTriggerNextTime() {
		return triggerNextTime;
	}
	public XxlJobInfo setTriggerNextTime(long triggerNextTime) {
		this.triggerNextTime = triggerNextTime;
		return this;
	}
	/**
	 * 至少执行一次
	 */
	public boolean isLeastOnce() {
		return leastOnce;
	}
	/**
	 * 至少执行一次
	 */
	public XxlJobInfo setLeastOnce(boolean leastOnce) {
		this.leastOnce = leastOnce;
		return this;
	}

	@Override
	public String toString() {
		return "{id:" + id + ", jobGroup:" + jobGroup + ", jobCron:" + jobCron + ", jobDesc:" + jobDesc + ", appName:" + appName + ", addTime:" + addTime + ", updateTime:" + updateTime + ", author:" + author + ", alarmEmail:" + alarmEmail
				+ ", executorRouteStrategy:" + executorRouteStrategy + ", executorHandler:" + executorHandler + ", executorParam:" + executorParam + ", executorBlockStrategy:" + executorBlockStrategy + ", executorTimeout:" + executorTimeout
				+ ", executorFailRetryCount:" + executorFailRetryCount + ", glueType:" + glueType + ", glueSource:" + glueSource + ", glueRemark:" + glueRemark + ", glueUpdatetime:" + glueUpdatetime + ", childJobId:" + childJobId + ", triggerStatus:"
				+ triggerStatus + ", triggerLastTime:" + triggerLastTime + ", triggerNextTime:" + triggerNextTime + "}";
	}
}
