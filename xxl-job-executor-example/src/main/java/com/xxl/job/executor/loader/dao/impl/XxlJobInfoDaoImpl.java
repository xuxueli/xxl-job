package com.xxl.job.executor.loader.dao.impl;

import java.util.HashMap;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.xxl.job.executor.loader.dao.IXxlJobInfoDao;
import com.xxl.job.executor.loader.dao.model.XxlJobInfo;


/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:17:52
 */
@Repository
public class XxlJobInfoDaoImpl implements IXxlJobInfoDao {

	@Resource
	public SqlSessionTemplate sqlSessionTemplate;

	@Override
	public XxlJobInfo load(String jobGroup, String jobName) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("jobGroup", jobGroup);
		params.put("jobName", jobName);
		return sqlSessionTemplate.selectOne("XxlJobInfoMapper.load", params);
	}
	
	
}
