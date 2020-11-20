package com.xxl.job.core.handler;

/**
 * job handler
 *
 * @author xuxueli 2015-12-19 19:06:38
 */
public interface IJobHandler {

    /**
     * execute handler, invoked when executor receives a scheduling request
     *
     * @throws Exception
     */
    void execute() throws Exception;

    /**
     * init handler, invoked when JobThread init
     */
    default void init() throws Exception {
        // do something
    }

    /**
     * destroy handler, invoked when JobThread destroy
     */
    default void destroy() throws Exception {
        // do something
    }

}
