package com.xxl.job.dao;

import com.xxl.job.dao.model.XxlJobInfo;

/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:04:56
 */
public interface IXxlJobInfoDao {
	
	public XxlJobInfo load(String jobGroup, String jobName);

}
