package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogGlue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:04:56
 */
@Mapper
public interface XxlJobLogGlueDao {
	
	public int save(XxlJobLogGlue xxlJobLogGlue);
	
	public List<XxlJobLogGlue> findByJobId(@Param("jobId") long jobId);

	public int removeOld(@Param("jobId") long jobId,@Param("recentLogIds") List<Long> recentLogIds);

	public int deleteByJobId(@Param("jobId") long jobId);

	public List<Long> findIds(@Param("jobId") long jobId);
	
}
