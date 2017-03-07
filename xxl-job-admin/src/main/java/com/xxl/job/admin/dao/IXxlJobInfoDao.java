package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface IXxlJobInfoDao {

	public List<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, String executorHandler);
	public int pageListCount(int offset, int pagesize, int jobGroup, String executorHandler);
	
	public int save(XxlJobInfo info);
	
	public XxlJobInfo load(int jobGroup, String jobName);
	
	public int update(XxlJobInfo item);
	
	public int delete(int jobGroup, String jobName);

	public List<XxlJobLog> getJobsByGroup(String jobGroup);
}
