package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 * @author xuxueli 2016-1-12 18:03:06
 */
public interface IXxlJobLogDao {
	
	public List<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd);
	public int pageListCount(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd);
	
	public XxlJobLog load(int id);

	public int save(XxlJobLog xxlJobLog);

	public int updateTriggerInfo(XxlJobLog xxlJobLog);

	public int updateHandleInfo(XxlJobLog xxlJobLog);
	
	public int delete(int jobId);

	public int triggerCountByHandleCode(int handleCode);

	public List<Map<String, Object>> triggerCountByDay(Date from, Date to, int handleCode);

	public int clearLog(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum);

}
