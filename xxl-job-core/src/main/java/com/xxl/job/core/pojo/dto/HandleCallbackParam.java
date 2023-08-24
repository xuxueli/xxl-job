package com.xxl.job.core.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 处理回调参数
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Data
@AllArgsConstructor
public class HandleCallbackParam implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * 日志ID
     */
    private long logId;

    /**
     * 日志时间
     */
    private long logDateTime;
    
    /**
     * 执行-状态(-1: 运行中,0:成功,其他:失败)
     */
    private Integer handleCode;

    /**
     * 执行结果
     */
    private String handleMessage;


}
