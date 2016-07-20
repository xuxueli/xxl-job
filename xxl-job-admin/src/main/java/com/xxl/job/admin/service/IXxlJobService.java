package com.xxl.job.admin.service;

import java.util.Map;

import com.xxl.job.admin.core.model.ReturnT;

/**
 * core job service for xxl-job
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface IXxlJobService {
	
	public Map<String, Object> pageList(int start, int length, String jobGroup, String jobDesc, String filterTime);
	
	public ReturnT<String> add(String jobGroup, String jobCron, String jobDesc,
			String executorAddress,	String executorParam, String author, String alarmEmail,
			int glueSwitch, String glueSource, String glueRemark);
	
	public ReturnT<String> reschedule(String jobGroup, String jobName, String jobCron, String jobDesc,
			String handler_address, String handler_params, String author, String alarmEmail);
	
	public ReturnT<String> remove(String jobGroup, String jobName);
	
	public ReturnT<String> pause(String jobGroup, String jobName);
	
	public ReturnT<String> resume(String jobGroup, String jobName);
	
	public ReturnT<String> triggerJob(String jobGroup, String jobName);
	
}
