package com.xxl.job.executor.factory.handler;

/**
 * job处理程序
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
public abstract class JobHandler {

    /**
     * 初始化
     *
     * @throws Exception 异常
     */
    public abstract void init() throws Exception;

    /**
     * 执行任务
     *
     * @param param 参数
     * @throws Exception 异常
     */
    public abstract void execute(Object param) throws Exception;

    /**
     * 销毁
     *
     * @throws Exception 异常
     */
    public abstract void destroy() throws Exception;


}
