package com.xxl.job.service.job;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * http job bean
 * @author xuxueli 2015-12-17 18:20:34
 */

@DisallowConcurrentExecution	// 串行；线程数要多配置几个，否则不生效；
public class LocalJobBean extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(LocalJobBean.class);

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
		
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		logger.info(">>>>>>>>>>> xxl-job run :jobId:{}, group:{}, jobDataMap:{}", 
				new Object[]{triggerKey, triggerGroup, jobDataMap});
    }
	
}