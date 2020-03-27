package com.xxl.job.admin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xxl.job.admin.core.model.XxlAlarmInfo;

/**
 * alarm info
 * 
 * @author Locki 2020-03-26
 *
 */
@Mapper
public interface XxlAlarmInfoDao {

	public List<XxlAlarmInfo> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize,
			@Param("alarmEnum") String alarmEnum, @Param("alarmName") String alarmName, @Param("alarmDesc") String alarmDesc);

	public int pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize,
			@Param("alarmEnum") String alarmEnum, @Param("alarmName") String alarmName, @Param("alarmDesc") String alarmDesc);

	public void save(XxlAlarmInfo alarmInfo);

	public XxlAlarmInfo loadById(@Param("id")long id);

	public void update(XxlAlarmInfo alarmInfo);

	public List<XxlAlarmInfo> findAll();
	
	public List<XxlAlarmInfo> listInfo(long[] ids);
	
	public void delete(@Param("id")long id);
}
