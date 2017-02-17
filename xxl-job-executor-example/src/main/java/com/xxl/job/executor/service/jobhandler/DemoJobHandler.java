package com.xxl.job.executor.service.jobhandler;

import java.util.HashMap;
import java.util.List;

import org.beetl.sql.core.SQLManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.executor.model.Log;


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
	@Autowired
	SQLManager sqlManager;
	@Override
	public void execute(String... params) throws Exception {
		for (String string : params) {
			logger.info(string);
		}
		List<Log> logs=sqlManager.select("log.all", Log.class, new HashMap<>());
		
		for (Log log : logs) {
			logger.info("job {} at:{}", log.getJobName(),log.getHandleTime());
		}
		
	}
	
}
