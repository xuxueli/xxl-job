package com.xxl.job.admin.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Date;

/**
 * xxl-job log, used to track trigger process
 *
 * @author xuxueli  2015-12-19 23:19:09
 */
@TableName("xxl_job_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XxlJobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID - 自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * job info - 执行器主键ID
     */
    private int jobGroup;

    /**
     * job info - 任务主键ID
     */
    private int jobId;

    /**
     * execute info - 执行器地址
     */
    private String executorAddress;

    /**
     * execute info - 执行器Handler名称
     */
    private String executorHandler;

    /**
     * execute info - 执行器参数
     */
    private String executorParam;

    /**
     * execute info - 执行器分片参数
     */
    private String executorShardingParam;

    /**
     * execute info - 失败重试次数
     */
    private int executorFailRetryCount;

    /**
     * trigger info - 触发时间
     */
    private Date triggerTime;

    /**
     * trigger info - 触发状态
     */
    private int triggerCode;

    /**
     * trigger info - 触发消息
     */
    private String triggerMsg;

    /**
     * handle info - 处理时间
     */
    private Date handleTime;

    /**
     * handle info - 处理状态
     */
    private int handleCode;

    /**
     * handle info - 处理消息
     */
    private String handleMsg;

    /**
     * alarm info - 告警状态
     */
    private int alarmStatus;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getJobGroup() { return jobGroup; }
    public void setJobGroup(int jobGroup) { this.jobGroup = jobGroup; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public String getExecutorAddress() { return executorAddress; }
    public void setExecutorAddress(String executorAddress) { this.executorAddress = executorAddress; }

    public String getExecutorHandler() { return executorHandler; }
    public void setExecutorHandler(String executorHandler) { this.executorHandler = executorHandler; }

    public String getExecutorParam() { return executorParam; }
    public void setExecutorParam(String executorParam) { this.executorParam = executorParam; }

    public String getExecutorShardingParam() { return executorShardingParam; }
    public void setExecutorShardingParam(String executorShardingParam) { this.executorShardingParam = executorShardingParam; }

    public int getExecutorFailRetryCount() { return executorFailRetryCount; }
    public void setExecutorFailRetryCount(int executorFailRetryCount) { this.executorFailRetryCount = executorFailRetryCount; }

    public Date getTriggerTime() { return triggerTime; }
    public void setTriggerTime(Date triggerTime) { this.triggerTime = triggerTime; }

    public int getTriggerCode() { return triggerCode; }
    public void setTriggerCode(int triggerCode) { this.triggerCode = triggerCode; }

    public String getTriggerMsg() { return triggerMsg; }
    public void setTriggerMsg(String triggerMsg) { this.triggerMsg = triggerMsg; }

    public Date getHandleTime() { return handleTime; }
    public void setHandleTime(Date handleTime) { this.handleTime = handleTime; }

    public int getHandleCode() { return handleCode; }
    public void setHandleCode(int handleCode) { this.handleCode = handleCode; }

    public String getHandleMsg() { return handleMsg; }
    public void setHandleMsg(String handleMsg) { this.handleMsg = handleMsg; }

    public int getAlarmStatus() { return alarmStatus; }
    public void setAlarmStatus(int alarmStatus) { this.alarmStatus = alarmStatus; }
}