package com.xxl.job.admin.service;


import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.Date;
import java.util.Map;

/**
 * core job action for xxl-job
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface XxlJobService {

	/**
	 * page list
	 */
	Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

	/**
	 * add job
	 */
	ReturnT<String> add(XxlJobInfo jobInfo);

	/**
	 * update job
	 */
	ReturnT<String> update(XxlJobInfo jobInfo);

	/**
	 * remove job
	 * *
	 */
	ReturnT<String> remove(int id);

	/**
	 * start job
	 */
	ReturnT<String> start(int id);

	/**
	 * stop job
	 */
	ReturnT<String> stop(int id);

	/**
	 * trigger
	 */
	ReturnT<String> trigger(XxlJobUser loginUser, int jobId, String executorParam, String addressList);

	/**
	 * dashboard info
	 */
	Map<String, Object> dashboardInfo();

	/**
	 * chart info
	 */
	ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate);

}
