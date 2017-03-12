package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.dao.IXxlJobLogGlueDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

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
	public List<XxlJobLogGlue> findByJobId(int jobId) {
		return sqlSessionTemplate.selectList("XxlJobLogGlueMapper.findByJobId", jobId);
	}

	@Override
	public int removeOld(int jobId, int limit) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("jobId", jobId);
		params.put("limit", limit);
		return sqlSessionTemplate.delete("XxlJobLogGlueMapper.removeOld", params);
	}

	@Override
	public int deleteByJobId(int jobId) {
		return sqlSessionTemplate.delete("XxlJobLogGlueMapper.deleteByJobId", jobId);
	}
	
}
