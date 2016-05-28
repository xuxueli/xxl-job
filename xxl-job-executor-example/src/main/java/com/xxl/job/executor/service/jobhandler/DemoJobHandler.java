package com.xxl.job.executor.service.jobhandler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;


/**
 * 任务Handler的一个Demo（Bean模式）
 * 
 * 开发步骤：
 * 1、继承 “IJobHandler” ；
 * 2、装配到Spring，例如加 “@Service” 注解；
 * 3、加 “@JobHander” 注解，自定义属性name的值；name值在配置新任务是使用；
 * 
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHander(name="demoJobHandler")
@Service
public class DemoJobHandler extends IJobHandler {
	private static transient Logger logger = LoggerFactory.getLogger(DemoJobHandler.class);
	
	@Override
	public JobHandleStatus execute(String... params) throws Exception {
		logger.info("XXL-JOB, Hello World.");
		
		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			TimeUnit.SECONDS.sleep(2);
		}
		return JobHandleStatus.SUCCESS;
	}
	
}
