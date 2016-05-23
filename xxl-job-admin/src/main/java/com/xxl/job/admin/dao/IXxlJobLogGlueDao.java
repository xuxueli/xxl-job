package com.xxl.job.admin.dao;

import java.util.List;

import com.xxl.job.admin.core.model.XxlJobLogGlue;

/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:04:56
 */
public interface IXxlJobLogGlueDao {
	
	public int save(XxlJobLogGlue xxlJobLogGlue);
	
	public List<XxlJobLogGlue> selectList(String jobGroup, String jobName);

	public int removeOld(String jobGroup, String jobName, int limit);

	public int delete(String jobGroup, String jobName);
	
}
