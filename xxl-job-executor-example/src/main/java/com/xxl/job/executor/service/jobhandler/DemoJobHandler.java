package com.xxl.job.executor.service.jobhandler;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * 任务Handler的一个Demo（Bean模式）
 * 
 * 开发步骤：
 * 1、继承 “IJobHandler” ；
 * 2、装配到Spring，例如加 “@Service” 注解；
 * 3、加 “@JobHander” 注解，注解value值为新增任务生成的JobKey的值;多个JobKey用逗号分割;
 * 
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHander(value="demoJobHandler")
@Service
public class DemoJobHandler extends IJobHandler {
	private static transient Logger logger = LoggerFactory.getLogger(DemoJobHandler.class);
	
	@Override
	public void execute(String... params) throws Exception {
		logger.info("XXL-JOB, Hello World.");
		
		for (int i = 0; i < 5; i++) {
			logger.info("beat at:{}", i);
			TimeUnit.SECONDS.sleep(2);
		}
	}
	
}
