package com.xxl.job.dao;


import java.util.List;

import com.xxl.job.core.model.XxlJobLog;

public interface IXxlJobLogDao {
	
	public int save(XxlJobLog xxlJobLog);
	
	public XxlJobLog load(int id);
	
	public int updateTriggerInfo(XxlJobLog xxlJobLog);
	
	public int updateHandleInfo(XxlJobLog xxlJobLog);
	
	public List<XxlJobLog> pageList(int offset, int pagesize,String jobName);
	
	public int pageListCount(int offset, int pagesize,String jobName);
	
}
