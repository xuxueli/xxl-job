package com.xxl.job.core.handler.impl;

import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;

/**
 * glue job handler
 * @author xuxueli 2016-5-19 21:05:45
 */
public class GlueJobHandler extends IJobHandler {
	
	private int jobId;
	public GlueJobHandler(int jobId) {
		this.jobId = jobId;
	}

	@Override
	public void execute(String... params) throws Exception {
		GlueFactory.glue(jobId, params);
	}

}
