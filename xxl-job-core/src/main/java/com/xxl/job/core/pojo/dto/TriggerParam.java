package com.xxl.job.core.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 触发参数
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Data
public class TriggerParam implements Serializable{
    private static final long serialVersionUID = 42L;

    private Long jobId;

    private String executorHandler;
    private String executorParams;
    private String executorBlockStrategy;
    private int executorTimeout;

    private long logId;
    private long logDateTime;

    private String glueType;
    private String glueSource;
    private long glueUpdatedTime;

    private int broadcastIndex;
    private int broadcastTotal;



}
