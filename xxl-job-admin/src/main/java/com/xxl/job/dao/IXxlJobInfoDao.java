package com.xxl.job.dao;

import java.util.Date;
import java.util.List;

import com.xxl.job.core.model.XxlJobInfo;

/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface IXxlJobInfoDao {

	public List<XxlJobInfo> pageList(int offset, int pagesize, String jobName, Date addTimeStart, Date addTimeEnd);
	public int pageListCount(int offset, int pagesize, String jobName, Date addTimeStart, Date addTimeEnd);
	
	public int save(XxlJobInfo info);
	
	public XxlJobInfo load(String jobName);
	
	public int update(XxlJobInfo item);
	
	public int delete(String jobName);
	
}
