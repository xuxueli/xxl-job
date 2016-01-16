package com.xxl.job.service.job.impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.service.job.LocalNomalJobBean;

public class DemoNomalJobBean extends LocalNomalJobBean {
	private static Logger Logger = LoggerFactory.getLogger(DemoNomalJobBean.class);
	
	@Override
	public Object handle(String... param) {
		Logger.info("DemoNomalJobBean run :" + param);
		
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return false;
	}

}
