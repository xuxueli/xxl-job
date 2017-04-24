package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobInfo;

import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface IXxlJobInfoDao {

	public List<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, String executorHandler);
	public int pageListCount(int offset, int pagesize, int jobGroup, String executorHandler);
	
	public int save(XxlJobInfo info);

	public XxlJobInfo loadById(int id);
	
	public int update(XxlJobInfo item);
	
	public int delete(int id);

	public List<XxlJobInfo> getJobsByGroup(String jobGroup);

	public int findAllCount();

}
