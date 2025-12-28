package com.xxl.job.admin.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
@Data
@NoArgsConstructor
public class XxlJobInfo {
	
	private int id;				// 主键ID
	
	private int jobGroup;		// 执行器主键ID
	private String jobDesc;
	
	private Date addTime;
	private Date updateTime;
	
	private String author;		// 负责人
	private String alarmEmail;	// 报警邮件

	private String scheduleType;			// 调度类型：ScheduleTypeEnum
	private String scheduleConf;			// 调度配置，值含义取决于调度类型
	private String misfireStrategy;			// 调度过期策略：MisfireStrategyEnum

	private String executorRouteStrategy;	// 执行器路由策略：ExecutorRouteStrategyEnum
	private String executorHandler;		    // 执行器，任务Handler名称
	private String executorParam;		    // 执行器，任务参数
	private String executorBlockStrategy;	// 阻塞处理策略：ExecutorBlockStrategyEnum
	private int executorTimeout;     		// 任务执行超时时间，单位秒
	private int executorFailRetryCount;		// 失败重试次数
	
	private String glueType;		// GLUE类型：GlueTypeEnum
	private String glueSource;		// GLUE源代码
	private String glueRemark;		// GLUE备注
	private Date glueUpdatetime;	// GLUE更新时间

	private String childJobId;		// 子任务ID，多个逗号分隔

	private int triggerStatus;		// 调度状态：TriggerStatus
	private long triggerLastTime;	// 上次调度时间
	private long triggerNextTime;	// 下次调度时间


	private String remark; // 备注

	private Date newestTriggerTime; // 最新一次调度时间，仅列表显示使用
	private Integer newestLogStatus; // 最新一次调度状态，仅列表显示使用

}
