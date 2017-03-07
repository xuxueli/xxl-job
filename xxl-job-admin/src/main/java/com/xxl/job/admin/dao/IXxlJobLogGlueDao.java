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
public interface IXxlJobLogGlueDao {
	
	public int save(XxlJobLogGlue xxlJobLogGlue);
	
	public List<XxlJobLogGlue> selectList(@Param("jobGroup") int jobGroup, @Param("jobName")String jobName);

	public int removeOld(@Param("jobGroup")int jobGroup, @Param("jobName")String jobName, @Param("limit")int limit);

	public int delete(@Param("jobGroup")int jobGroup, @Param("jobName")String jobName);
	
}
