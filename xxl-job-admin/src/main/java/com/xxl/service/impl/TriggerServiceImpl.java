package com.xxl.service.impl;

import java.util.Date;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xxl.service.ITriggerService;

/**
 * Trigger Service
 * @author xuxueli
 */
@Service("triggerService")
public class TriggerServiceImpl implements ITriggerService {
	private static transient Logger logger = LoggerFactory.getLogger(TriggerServiceImpl.class);
	
	
	/**
	 * 全站静态化
	 */
	public void generateNetHtml() {
		logger.info("全站静态化 run at :{}", FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}
	
}
