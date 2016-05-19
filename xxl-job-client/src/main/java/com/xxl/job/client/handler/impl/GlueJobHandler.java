package com.xxl.job.client.handler.impl;

import com.xxl.job.client.glue.GlueFactory;
import com.xxl.job.client.handler.IJobHandler;

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
	public JobHandleStatus handle(String... params) throws Exception {
		return GlueFactory.glue(job_group, job_name, params);
	}

}
