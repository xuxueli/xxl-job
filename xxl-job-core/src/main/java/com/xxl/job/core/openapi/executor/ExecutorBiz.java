package com.xxl.job.core.openapi.executor;

import com.xxl.job.core.openapi.executor.dto.*;
import com.xxl.tool.response.Response;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface ExecutorBiz {

    /**
     * beat
     *
     * @return response
     */
    public Response<String> beat();

    /**
     * idle beat
     *
     * @param idleBeatRequest idleBeatRequest
     * @return response
     */
    public Response<String> idleBeat(IdleBeatRequest idleBeatRequest);

    /**
     * run
     *
     * @param triggerRequest triggerRequest
     * @return response
     */
    public Response<String> trigger(TriggerRequest triggerRequest);

    /**
     * kill
     *
     * @param killRequest killRequest
     * @return response
     */
    public Response<String> kill(KillRequest killRequest);

    /**
     * log
     *
     * @param logRequest logRequest
     * @return response
     */
    public Response<LogData> log(LogRequest logRequest);

}
