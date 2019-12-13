package com.xxl.job.admin.dao;

import com.github.pagehelper.Page;
import com.xxl.job.admin.core.model.XxlJobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface XxlJobInfoDao {

	public int pageListCount(XxlJobInfo j);
	public Page<XxlJobInfo> select(XxlJobInfo j);

	public int save(XxlJobInfo info);

	public XxlJobInfo loadById(@Param("id") int id);

	public int update(XxlJobInfo xxlJobInfo);

	public int delete(@Param("id") long id);

	public Page<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

	public int findAllCount();

	public List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize );

	public int scheduleUpdate(XxlJobInfo xxlJobInfo);
}
