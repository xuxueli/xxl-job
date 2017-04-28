package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;

/**
 * remote job handler
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class IJobHandler {

	/**
	 * job handler
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public abstract ReturnT<String> execute(String... params) throws Exception;
	
}
