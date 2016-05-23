package com.xxl.job.executor.loader;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xxl.job.core.glue.loader.GlueLoader;
import com.xxl.job.executor.loader.dao.IXxlJobInfoDao;
import com.xxl.job.executor.loader.dao.model.XxlJobInfo;

/**
 * GLUE 代码加载器，推荐将该服务配置成RPC服务
 * @author xuxueli
 */
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
