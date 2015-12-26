package com.xxl.job.service.job;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * http job bean
 * @author xuxueli 2015-12-17 18:20:34
 */
public class DemoJobBeanB extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(DemoJobBeanB.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		
		String triggerKey = context.getTrigger().getKey().getName();
		String triggerGroup = context.getTrigger().getKey().getGroup();
		Map<String, Object> jobDataMap = context.getMergedJobDataMap().getWrappedMap();
		
		// jobDataMap 2 params
		Map<String, String> params = new HashMap<String, String>();
		if (jobDataMap!=null && jobDataMap.size()>0) {
			for (Entry<String, Object> item : jobDataMap.entrySet()) {
				params.put(item.getKey(), String.valueOf(item.getValue()));
			}
		}
		
		logger.info(">>>>>>>>>>> xxl-job run :jobId:{}, group:{}, jobDataMap:{}", 
				new Object[]{triggerKey, triggerGroup, jobDataMap});
    }
	
}