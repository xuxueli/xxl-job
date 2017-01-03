package com.xxl.job.admin.service;

import java.util.Map;

import com.xxl.job.admin.core.model.ReturnT;

/**
 * core job action for xxl-job
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface IXxlJobService {
	/**
	 * 任务分页列表
	 * @param start
	 * @param length
	 * @param jobGroup
	 * @param executorHandler
	 * @param filterTime
	 * @return
	 */
	public Map<String, Object> pageList(int start, int length, int jobGroup, String executorHandler, String filterTime);
	/**
	 * 新增任务
	 * @param jobGroup
	 * @param jobCron
	 * @param jobDesc
	 * @param author
	 * @param alarmEmail
	 * @param executorAddress
	 * @param executorHandler
	 * @param executorParam
	 * @param glueSwitch
	 * @param glueSource
	 * @param glueRemark
	 * @param childJobKey
	 * @return
	 */
	public ReturnT<String> add(int jobGroup, String jobCron, String jobDesc,String author, String alarmEmail,
			String executorAddress,	String executorHandler, String executorParam,
			int glueSwitch, String glueSource, String glueRemark, String childJobKey);
	/**
	 * 任务更新并重排
	 * @param jobGroup
	 * @param jobName
	 * @param jobCron
	 * @param jobDesc
	 * @param author
	 * @param alarmEmail
	 * @param executorAddress
	 * @param executorHandler
	 * @param executorParam
	 * @param glueSwitch
	 * @param childJobKey
	 * @return
	 */
	public ReturnT<String> reschedule(int jobGroup, String jobName, String jobCron, String jobDesc, String author, String alarmEmail,
			String executorAddress, String executorHandler, String executorParam, int glueSwitch, String childJobKey);
	/**
	 * 删除任务
	 * @param jobGroup
	 * @param jobName
	 * @return
	 */
	public ReturnT<String> remove(int jobGroup, String jobName);
	/**
	 * 暂停任务
	 * @param jobGroup
	 * @param jobName
	 * @return
	 */
	public ReturnT<String> pause(int jobGroup, String jobName);
	/**
	 * 恢复任务
	 * @param jobGroup
	 * @param jobName
	 * @return
	 */
	public ReturnT<String> resume(int jobGroup, String jobName);
	/**
	 * 任务调用
	 * @param jobGroup
	 * @param jobName
	 * @return
	 */
	public ReturnT<String> triggerJob(int jobGroup, String jobName);
	
	/**
	 * 可更新参数的任务调用
	 * @param jobGroup
	 * @param jobName
	 * @param executorParam
	 * @return
	 */
	public ReturnT<String> triggerJob(int jobGroup, String jobName,String executorParam);
	
}
