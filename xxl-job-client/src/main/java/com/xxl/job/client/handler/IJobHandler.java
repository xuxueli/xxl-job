package com.xxl.job.client.handler;

import java.util.Map;

/**
 * remote job handler
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class IJobHandler extends HandlerRepository{
	
	/**
	 * job handler <br><br>
	 * the return Object will be and stored
	 * @param param
	 * @return 
	 * @throws Exception
	 */
	public abstract JobHandleStatus handle(Map<String, String> param) throws Exception;
	
	public enum JobHandleStatus{
		/**
		 * handle success
		 */
		SUCCESS, 
		/**
		 * handle fail
		 */
		FAIL, 
		/**
		 * handle not found
		 */
		NOT_FOUND;
	}
	
	public enum JobTriggerStatus{
		/**
		 * trigger success
		 */
		SUCCESS, 
		/**
		 * trigger fail
		 */
		FAIL;
	}
	
}
