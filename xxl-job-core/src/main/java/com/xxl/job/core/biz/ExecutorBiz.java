package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.*;

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
     * idle beat
     *
     * @param idleBeatRequest
     * @return
     */
    public ReturnT<String> idleBeat(IdleBeatRequest idleBeatRequest);

    /**
     * run
     * @param triggerRequest
     * @return
     */
    public ReturnT<String> run(TriggerRequest triggerRequest);

    /**
     * kill
     * @param killRequest
     * @return
     */
    public ReturnT<String> kill(KillRequest killRequest);

    /**
     * log
     * @param logRequest
     * @return
     */
    public ReturnT<LogResult> log(LogRequest logRequest);

}
