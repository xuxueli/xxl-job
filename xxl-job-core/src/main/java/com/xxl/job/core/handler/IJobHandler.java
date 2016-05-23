package com.xxl.job.core.handler;

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
	public abstract JobHandleStatus execute(String... params) throws Exception;
	
	public static enum JobHandleStatus{
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
	
}
