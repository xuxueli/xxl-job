package com.xxl.job.admin.service;


import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.Map;

/**
 * core job action for xxl-job
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface IXxlJobService {
	
	public Map<String, Object> pageList(int start, int length, int jobGroup, String executorHandler, String filterTime);
	
	public ReturnT<String> add(XxlJobInfo jobInfo);
	
	public ReturnT<String> reschedule(XxlJobInfo jobInfo);
	
	public ReturnT<String> remove(int jobGroup, String jobName);
	
	public ReturnT<String> pause(int jobGroup, String jobName);
	
	public ReturnT<String> resume(int jobGroup, String jobName);
	
	public ReturnT<String> triggerJob(int jobGroup, String jobName);
	
}
