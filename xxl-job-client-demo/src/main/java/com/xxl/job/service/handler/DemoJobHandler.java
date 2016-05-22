package com.xxl.job.service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xxl.job.client.handler.IJobHandler;
import com.xxl.job.client.handler.annotation.JobHander;

/**
 * demo job handler
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHander(jobName="demoJobHandler")
@Service
public class DemoJobHandler extends IJobHandler {
	private static transient Logger logger = LoggerFactory.getLogger(DemoJobHandler.class);
	
	@Override
	public JobHandleStatus handle(String... params) throws Exception {
		logger.info("XXL-JOB, Hello World.");
		return JobHandleStatus.SUCCESS;
	}
	
}
