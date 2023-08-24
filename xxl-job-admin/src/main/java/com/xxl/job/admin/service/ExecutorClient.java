package com.xxl.job.admin.service;

import com.xxl.job.core.pojo.dto.IdleBeatParam;
import com.xxl.job.core.pojo.dto.KillParam;
import com.xxl.job.core.pojo.dto.LogParam;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.LogResult;
import com.xxl.job.core.pojo.vo.ResponseVO;

/**
 * 执行程序客户端
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
public interface ExecutorClient {

    /**
     * 心跳
     *
     * @param address 地址
     * @return {@link Boolean}
     */
    Boolean beat(String address);

    /**
     * 空闲心跳
     *
     * @param address       地址
     * @param idleBeatParam 闲置击败参数
     * @return {@link Boolean}
     */
    Boolean idleBeat(String address, IdleBeatParam idleBeatParam);

    /**
     * 运行
     *
     * @param address 地址
     * @param param   参数
     * @return {@link ResponseVO}
     */
    ResponseVO run(String address, TriggerParam param);

    /**
     * 停止
     *
     * @param address 地址
     * @param param   参数
     * @return {@link ResponseVO}
     */
    ResponseVO kill(String address, KillParam param);

    /**
     * 日志
     *
     * @param logParam 日志参数
     * @param address  地址
     * @return {@link LogResult}
     */
    LogResult log(String address, LogParam logParam);


}
