package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;

/**
 *  multiplex job handler
 *
 * @author xuemc 2018-11-16
 */
public abstract class AbstractMultiJobHandler implements IJobHandler{


	/** success */
	public static final ReturnT<String> SUCCESS = new ReturnT<String>(200, null);
	/** fail */
	public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);
	/** fail timeout */
	public static final ReturnT<String> FAIL_TIMEOUT = new ReturnT<String>(502, null);


	/**
	 * execute handler, invoked when executor receives a scheduling request
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public abstract ReturnT<String> execute(String param) throws Exception;


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
