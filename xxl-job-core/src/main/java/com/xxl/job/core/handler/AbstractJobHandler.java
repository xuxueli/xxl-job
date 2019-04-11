package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;

/**
 * job handler
 *
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class AbstractJobHandler implements IJobHandler{


	/** success */
	public static final ReturnT<String> SUCCESS = new ReturnT<String>(200, null);
	/** fail */
	public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);
	/** fail timeout */
	public static final ReturnT<String> FAIL_TIMEOUT = new ReturnT<String>(502, null);

	/**
	 * init handler, invoked when JobThread init
	 */
	public void init() {
		// TODO
	}


	/**
	 * destroy handler, invoked when JobThread destroy
	 */
	public void destroy() {
		// TODO
	}


}
