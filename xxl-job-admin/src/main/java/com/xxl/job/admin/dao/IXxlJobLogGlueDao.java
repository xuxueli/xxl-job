package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogGlue;

import java.util.List;

/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:04:56
 */
public interface IXxlJobLogGlueDao {
	
	public int save(XxlJobLogGlue xxlJobLogGlue);
	
	public List<XxlJobLogGlue> findByJobId(int jobId);

	public int removeOld(int jobId, int limit);

	public int deleteByJobId(int jobId);
	
}
