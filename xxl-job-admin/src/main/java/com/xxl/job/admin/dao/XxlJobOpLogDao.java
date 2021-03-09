package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobOpLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XxlJobOpLogDao {

	public List<XxlJobOpLog> pageList(@Param("offset") int offset,
									 @Param("pagesize") int pagesize,
									 @Param("logType") String logType,
									 @Param("description") String description,
									 @Param("createUser") String createUser);

	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("logType") String logType,
							 @Param("description") String description,
							 @Param("createUser") String createUser);

	public int save(XxlJobOpLog xxlJobOpLog);

}
