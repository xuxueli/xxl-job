package com.xxl.job.service.handler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xxl.job.client.handler.IJobHandler;
import com.xxl.job.client.handler.JobHander;

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
		logger.info(" ... params:" + params);
		for (int i = 0; i < 5; i++) {
			TimeUnit.SECONDS.sleep(1);
			logger.info("handler run:{}", i);
		}
		return JobHandleStatus.SUCCESS;
	}
	
	public static void main(String[] args) {
		System.out.println(DemoJobHandler.class.getName());
		System.out.println(DemoJobHandler.class);
	}
	
}
