package com.xxl.job.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xxl.job.service.ITriggerService;

/**
 * local trigger, only exists in local jvm
 * @author xuxueli 2015-12-17 17:31:24
 */
@Service("triggerService")
public class TriggerServiceImpl implements ITriggerService {
	private static transient Logger logger = LoggerFactory.getLogger(TriggerServiceImpl.class);
	
	public void beat() {
		logger.info(">>>>>>>>>>> xxl-job beat success.");
	}
	
}
