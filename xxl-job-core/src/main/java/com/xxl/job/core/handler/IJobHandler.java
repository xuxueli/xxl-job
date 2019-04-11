package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;

public interface IJobHandler<T> {

	/**
	 * init handler, invoked when JobThread init
	 */
	void init(); 
	
	/**
	 * execute handler, invoked when executor receives a scheduling request
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	ReturnT<T> execute(String param) throws Exception;
	
	
	/**
	 * destroy handler, invoked when JobThread destroy
	 */
	void destroy();
	
}
