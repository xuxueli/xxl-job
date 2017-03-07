package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface IXxlJobInfoDao {

	public List<XxlJobInfo> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("jobGroup") int jobGroup,@Param("executorHandler") String executorHandler);
	public int pageListCount(@Param("offset")int offset, @Param("pagesize")int pagesize, @Param("jobGroup")int jobGroup, @Param("executorHandler")String executorHandler);
	
	public int save(XxlJobInfo info);
	
	public XxlJobInfo load(@Param("jobGroup")int jobGroup, @Param("jobName")String jobName);
	
	public int update(XxlJobInfo item);
	
	public int delete(@Param("jobGroup")int jobGroup, @Param("jobName")String jobName);

	public List<XxlJobLog> getJobsByGroup(String jobGroup);
}
