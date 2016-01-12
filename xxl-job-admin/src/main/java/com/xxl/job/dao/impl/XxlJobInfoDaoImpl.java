package com.xxl.job.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.xxl.job.core.model.XxlJobInfo;
import com.xxl.job.dao.IXxlJobInfoDao;

/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
@Repository
public class XxlJobInfoDaoImpl implements IXxlJobInfoDao {
	
	@Resource
	public SqlSessionTemplate sqlSessionTemplate;

	@Override
	public List<XxlJobInfo> pageList(int offset, int pagesize, String jobName, Date addTimeStart, Date addTimeEnd) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("pagesize", pagesize);
		params.put("jobName", jobName);
		params.put("addTimeStart", addTimeStart);
		params.put("addTimeEnd", addTimeEnd);
		
		return sqlSessionTemplate.selectList("XxlJobInfoMapper.pageList", params);
	}

	@Override
	public int pageListCount(int offset, int pagesize, String jobName, Date addTimeStart, Date addTimeEnd) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("pagesize", pagesize);
		params.put("jobName", jobName);
		params.put("addTimeStart", addTimeStart);
		params.put("addTimeEnd", addTimeEnd);
		
		return sqlSessionTemplate.selectOne("XxlJobInfoMapper.pageListCount", params);
	}

	@Override
	public int save(XxlJobInfo info) {
		return sqlSessionTemplate.insert("XxlJobInfoMapper.save", info);
	}

	@Override
	public XxlJobInfo load(String jobName) {
		return sqlSessionTemplate.selectOne("XxlJobInfoMapper.load", jobName);
	}

	@Override
	public int update(XxlJobInfo item) {
		return sqlSessionTemplate.update("XxlJobInfoMapper.update", item);
	}

	@Override
	public int delete(String jobName) {
		return sqlSessionTemplate.update("XxlJobInfoMapper.delete", jobName);
	}
	
}
