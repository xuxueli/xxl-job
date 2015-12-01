package com.xxl.service.job;

import java.util.Date;

import org.apache.commons.lang.time.FastDateFormat;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class JobDetailDemo extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(JobDetailDemo.class);
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		logger.info("全站静态化[DB] run at :{}", FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

}
