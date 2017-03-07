package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * job log
 * @author xuxueli 2016-1-12 18:03:06
 */
@Mapper
public interface IXxlJobLogDao {
	
	public List<XxlJobLog> pageList(@Param("offset") int offset, @Param("pagesize")int pagesize, @Param("jobGroup")int jobGroup,@Param("jobName") String jobName,@Param("triggerTimeStart") Date triggerTimeStart, @Param("triggerTimeEnd")Date triggerTimeEnd);
	public int pageListCount(@Param("offset")int offset, @Param("pagesize")int pagesize, @Param("jobGroup")int jobGroup, @Param("jobName")String jobName, @Param("triggerTimeStart")Date triggerTimeStart, @Param("triggerTimeEnd")Date triggerTimeEnd);
	
	public XxlJobLog load(int id);

	public int save(XxlJobLog xxlJobLog);
	public int updateTriggerInfo(XxlJobLog xxlJobLog);
	public int updateHandleInfo(XxlJobLog xxlJobLog);
	
	public int delete(@Param("jobGroup")int jobGroup, @Param("jobName")String jobName);
	
}
