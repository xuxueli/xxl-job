package com.xxl.job.admin.service;


import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.Map;

/**
 * core job action for xxl-job
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface XxlJobService {
	
	Map<String, Object> pageList(int start, int length, int jobGroup, String executorHandler, String filterTime);
	
	ReturnT<String> add(XxlJobInfo jobInfo);
	
	ReturnT<String> reschedule(XxlJobInfo jobInfo);
	
	ReturnT<String> remove(int id);
	
	ReturnT<String> pause(int id);
	
	ReturnT<String> resume(int id);
	
	ReturnT<String> triggerJob(int id);

	Map<String,Object> dashboardInfo();

	ReturnT<Map<String,Object>> triggerChartDate();

}
