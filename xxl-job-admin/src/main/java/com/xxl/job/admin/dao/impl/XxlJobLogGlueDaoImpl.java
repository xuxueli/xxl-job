package com.xxl.job.admin.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.dao.IXxlJobLogGlueDao;

/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:17:52
 */
@Repository
public class XxlJobLogGlueDaoImpl implements IXxlJobLogGlueDao {

	@Resource
	public SqlSessionTemplate sqlSessionTemplate;
	
	@Override
	public int save(XxlJobLogGlue xxlJobLogGlue) {
		return sqlSessionTemplate.insert("XxlJobLogGlueMapper.save", xxlJobLogGlue);
	}

	@Override
	public List<XxlJobLogGlue> selectList(String jobGroup, String jobName) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("jobGroup", jobGroup);
		params.put("jobName", jobName);
		return sqlSessionTemplate.selectList("XxlJobLogGlueMapper.selectList", params);
	}

	@Override
	public int removeOld(String jobGroup, String jobName, int limit) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("jobGroup", jobGroup);
		params.put("jobName", jobName);
		params.put("limit", limit);
		return sqlSessionTemplate.delete("XxlJobLogGlueMapper.removeOld", params);
	}

	@Override
	public int delete(String jobGroup, String jobName) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("jobGroup", jobGroup);
		params.put("jobName", jobName);
		return sqlSessionTemplate.delete("XxlJobLogGlueMapper.delete", params);
	}
	
}
