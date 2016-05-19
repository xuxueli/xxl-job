package com.xxl.job.service.loader;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xxl.job.client.glue.loader.GlueLoader;
import com.xxl.job.dao.IXxlJobInfoDao;
import com.xxl.job.dao.model.XxlJobInfo;

@Service("dbGlueLoader")
public class DbGlueLoader implements GlueLoader {

	@Resource
	private IXxlJobInfoDao xxlJobInfoDao;
	
	@Override
	public String load(String job_group, String job_name) {
		XxlJobInfo glue = xxlJobInfoDao.load(job_group, job_name);
		return glue!=null?glue.getGlueSource():null;
	}

}
