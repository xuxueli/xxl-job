package com.xxl.job.service.handler;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xxl.job.client.handler.HandlerRepository;
import com.xxl.job.client.handler.IJobHandler;

/**
 * demo job handler
 * @author xuxueli 2015-12-19 19:43:36
 */
@Service
public class DemoJobHandler extends IJobHandler {
	private static transient Logger logger = LoggerFactory.getLogger(DemoJobHandler.class);
	
	public DemoJobHandler() {
		HandlerRepository.regist(DemoJobHandler.class.getName(), this);
	}
	
	@Override
	public JobHandleStatus handle(Map<String, String> param) throws Exception {
		logger.info(" ... param:{}", param);
		TimeUnit.SECONDS.sleep(new Random().nextInt(5));
		return JobHandleStatus.SUCCESS;
	}
	
	public static void main(String[] args) {
		System.out.println(DemoJobHandler.class.getName());
		System.out.println(DemoJobHandler.class);
	}
	
}
