package com.xxl.job.core.handler.impl;

import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;

/**
 * glue job handler
 * @author xuxueli 2016-5-19 21:05:45
 */
public class GlueJobHandler extends IJobHandler {
	
	private String job_group;
	private String job_name;
	public GlueJobHandler(String job_group, String job_name) {
		this.job_group = job_group;
		this.job_name = job_name;
	}

	@Override
	public JobHandleStatus execute(String... params) throws Exception {
		return GlueFactory.glue(job_group, job_name, params);
	}

}
