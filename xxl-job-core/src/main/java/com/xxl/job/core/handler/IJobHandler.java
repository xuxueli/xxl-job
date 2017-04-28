package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * remote job handler
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class IJobHandler {
	private static Logger logger = LoggerFactory.getLogger(IJobHandler.class);

	/**
	 * append log
	 *
	 * @param appendLog
	 */
	public void log(String appendLog) {
		String logFileName = XxlJobFileAppender.contextHolder.get();
		XxlJobFileAppender.appendLog(logFileName, appendLog);
		logger.info("xxl-job log [{}]: {}", logFileName, appendLog);
	}
	
	/**
	 * job handler
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public abstract ReturnT<String> execute(String... params) throws Exception;
	
}
