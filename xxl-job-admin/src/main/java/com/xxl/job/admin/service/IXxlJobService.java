package com.xxl.job.admin.service;

import com.xxl.job.admin.core.model.ReturnT;

import java.util.Map;

/**
 * core job action for xxl-job
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface IXxlJobService {
	
	public Map<String, Object> pageList(int start, int length, String jobGroup, String executorHandler, String filterTime);
	
	public ReturnT<String> add(int jobGroup, String jobCron, String jobDesc,String author, String alarmEmail,
			String executorAddress,	String executorHandler, String executorParam,
			int glueSwitch, String glueSource, String glueRemark, String childJobKey);
	
	public ReturnT<String> reschedule(int jobGroup, String jobName, String jobCron, String jobDesc, String author, String alarmEmail,
			String executorAddress, String executorHandler, String executorParam, int glueSwitch, String childJobKey);
	
	public ReturnT<String> remove(String jobGroup, String jobName);
	
	public ReturnT<String> pause(String jobGroup, String jobName);
	
	public ReturnT<String> resume(String jobGroup, String jobName);
	
	public ReturnT<String> triggerJob(String jobGroup, String jobName);
	
}
