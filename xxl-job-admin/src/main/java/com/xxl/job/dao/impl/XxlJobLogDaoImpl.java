package com.xxl.job.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.xxl.job.core.model.XxlJobLog;
import com.xxl.job.dao.IXxlJobLogDao;

/**
 * job log
 * @author xuxueli 2016-1-12 18:03:06
 */
@Repository
public class XxlJobLogDaoImpl implements IXxlJobLogDao {
	
	@Resource
	public SqlSessionTemplate sqlSessionTemplate;

	@Override
	public List<XxlJobLog> pageList(int offset, int pagesize, String jobGroup, String jobName, Date triggerTimeStart, Date triggerTimeEnd) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("pagesize", pagesize);
		params.put("jobGroup", jobGroup);
		params.put("jobName", jobName);
		params.put("triggerTimeStart", triggerTimeStart);
		params.put("triggerTimeEnd", triggerTimeEnd);
		
		return sqlSessionTemplate.selectList("XxlJobLogMapper.pageList", params);
	}

	@Override
	public int pageListCount(int offset, int pagesize, String jobGroup, String jobName, Date triggerTimeStart, Date triggerTimeEnd) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("pagesize", pagesize);
		params.put("jobGroup", jobGroup);
		params.put("jobName", jobName);
		params.put("triggerTimeStart", triggerTimeStart);
		params.put("triggerTimeEnd", triggerTimeEnd);
		
		return sqlSessionTemplate.selectOne("XxlJobLogMapper.pageListCount", params);
	}

	@Override
	public XxlJobLog load(int id) {
		return sqlSessionTemplate.selectOne("XxlJobLogMapper.load", id);
	}
	
	@Override
	public XxlJobLog loadByGroupAndName(String jobGroup, String jobName) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("jobGroup", jobGroup);
		params.put("jobName", jobName);
		return sqlSessionTemplate.selectOne("XxlJobLogMapper.loadByGroupAndName", params);
	}

	@Override
	public int save(XxlJobLog xxlJobLog) {
		if (xxlJobLog!=null && xxlJobLog.getJobData().length()>2000) {
			xxlJobLog.setJobData(xxlJobLog.getJobData().substring(0, 2000));
		}
		return sqlSessionTemplate.insert("XxlJobLogMapper.save", xxlJobLog);
	}
	
	@Override
	public int updateTriggerInfo(XxlJobLog xxlJobLog) {
		if (xxlJobLog!=null && xxlJobLog.getTriggerMsg().length()>2000) {
			xxlJobLog.setTriggerMsg(xxlJobLog.getTriggerMsg().substring(0, 2000));
		}
		return sqlSessionTemplate.update("XxlJobLogMapper.updateTriggerInfo", xxlJobLog);
	}

	@Override
	public int updateHandleInfo(XxlJobLog xxlJobLog) {
		if (xxlJobLog!=null && xxlJobLog.getHandleMsg().length()>2000) {
			xxlJobLog.setHandleMsg(xxlJobLog.getHandleMsg().substring(0, 2000));
		}
		return sqlSessionTemplate.update("XxlJobLogMapper.updateHandleInfo", xxlJobLog);
	}
	
}
