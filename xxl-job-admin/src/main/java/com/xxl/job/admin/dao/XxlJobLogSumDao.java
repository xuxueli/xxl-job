package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogSum;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * XxlJobLogSumDao
 * @author Lei Xu 
 * 2022-11-12 18:03:06
 */
@Mapper
public interface XxlJobLogSumDao {

	// exist jobId not use jobGroup, not exist use jobGroup
	public List<XxlJobLogSum> pageList(
									@Param("jobGroup") int jobGroup,
									@Param("jobId") int jobId,
									@Param("triggerTimeStart") Date triggerTimeStart,
									@Param("triggerTimeEnd") Date triggerTimeEnd,
									@Param("logStatus") int logStatus);
}
