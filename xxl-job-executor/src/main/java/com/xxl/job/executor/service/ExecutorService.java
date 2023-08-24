package com.xxl.job.executor.service;

import com.xxl.job.core.pojo.dto.IdleBeatParam;
import com.xxl.job.core.pojo.dto.KillParam;
import com.xxl.job.core.pojo.dto.LogParam;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.LogResult;
import com.xxl.job.core.pojo.vo.ResponseVO;

/**
 * 执行器service
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
public interface ExecutorService {

    /**
     * beat
     *
     * @return {@link ResponseVO}
     */
    ResponseVO beat();

    /**
     * idle beat
     *
     * @param idleBeatParam 闲置击败参数
     * @return {@link ResponseVO}
     */
    ResponseVO idleBeat(IdleBeatParam idleBeatParam);

    /**
     * 运行
     *
     * @param triggerParam 触发参数
     * @return {@link ResponseVO}
     */
    ResponseVO run(TriggerParam triggerParam);

    /**
     * kill
     *
     * @param killParam 杀死参数
     * @return {@link ResponseVO}
     */
    ResponseVO kill(KillParam killParam);

    /**
     * 日志
     *
     * @param logParam 日志参数
     * @return {@link ResponseVO}<{@link LogResult}>
     */
    ResponseVO<LogResult> log(LogParam logParam);



}
