package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;

/**
 * job handler
 *
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class IJobHandler {

    public static final ReturnT<String> SUCCESS = new ReturnT<>(200, null);

    public static final ReturnT<String> FAIL = new ReturnT<>(500, null);

    public static final ReturnT<String> FAIL_TIMEOUT = new ReturnT<>(502, null);

    public  ReturnT<String> execute(String param) throws Exception{
        return SUCCESS;
    }


    /**
     * execute handler, invoked when executor receives a scheduling request
     *
     * @throws Exception
     */
    public  void execute() throws Exception{
        execute(XxlJobHelper.getJobParam());
    }


	/*@Deprecated
	public abstract ReturnT<String> execute(String param) throws Exception;*/

    /**
     * init handler, invoked when JobThread init
     */
    public void init() throws Exception {
        // do something
    }


    /**
     * destroy handler, invoked when JobThread destroy
     */
    public void destroy() throws Exception {
        // do something
    }


}
