package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface ExecutorBiz {

    /**
     * beat
     * @return
     */
    public ReturnT<String> beat();

    /**
     * kill
     * @param jobGroup
     * @param jobName
     * @return
     */
    public ReturnT<String> kill(String jobGroup, String jobName);

    /**
     * log
     * @param logDateTim
     * @param logId
     * @return
     */
    public ReturnT<String> log(long logDateTim, int logId);

    /**
     * run
     * @param triggerParam
     * @return
     */
    public ReturnT<String> run(TriggerParam triggerParam);

}
