package com.xxl.job.admin.core.model;

import lombok.Data;

import java.util.Date;

/**
 * xxl-job info
 * @author xuxueli  2016-1-12 18:25:49
 */
@Data
public class XxlJobInfo {
	
	private int id;				// 主键ID	    (JobKey.name)
	
	private int jobGroup;		// 执行器主键ID	(JobKey.group)
	private String jobCron;		// 任务执行CRON表达式 【base on quartz】
	private String jobDesc;
	
	private Date addTime;
	private Date updateTime;
	
	private String author;		// 负责人
	private String alarmEmail;	// 报警邮件

	private String executorRouteStrategy;	// 执行器路由策略
	private String executorHandler;		    // 执行器，任务Handler名称
	private String executorParam;		    // 执行器，任务参数
	private String executorBlockStrategy;	// 阻塞处理策略
	private String executorFailStrategy;	// 失败处理策略
	
	private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	private String glueSource;		// GLUE源代码
	private String glueRemark;		// GLUE备注
	private Date glueUpdatetime;	// GLUE更新时间

	private String childJobKey;		// 子任务Key
	
	// copy from quartz
	private String jobStatus;		// 任务状态 【base on quartz】

	public XxlJobInfo merge(XxlJobInfo xxlJobInfo) {
		this.setJobCron(xxlJobInfo.getJobCron());
		this.setJobDesc(xxlJobInfo.getJobDesc());
		this.setAuthor(xxlJobInfo.getAuthor());
		this.setAlarmEmail(xxlJobInfo.getAlarmEmail());
		this.setExecutorRouteStrategy(xxlJobInfo.getExecutorRouteStrategy());
		this.setExecutorHandler(xxlJobInfo.getExecutorHandler());
		this.setExecutorParam(xxlJobInfo.getExecutorParam());
		this.setExecutorBlockStrategy(xxlJobInfo.getExecutorBlockStrategy());
		this.setExecutorFailStrategy(xxlJobInfo.getExecutorFailStrategy());
		this.setChildJobKey(xxlJobInfo.getChildJobKey());
		return this;
	}
}
