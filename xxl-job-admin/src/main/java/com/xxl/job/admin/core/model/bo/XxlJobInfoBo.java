package com.xxl.job.admin.core.model.bo;

import com.xxl.job.admin.core.model.XxlJobInfo;

import java.sql.Time;
import java.util.Date;

/**
 * 添加任务bo
 *
 * @author: Dao-yang.
 * @date: Created in 2025/6/30 10:57
 */
public class XxlJobInfoBo extends XxlJobInfo {

    /**
     * 任务结束时间, 指定任务结束时间后,将会添加一个定时结束该任务的任务
     */
    private Date endTime;

    /**
     * 结束任务的执行器 handler, 指定任务结束时间后,该字段必传
     */
    private String endExecutorHandler;            // 执行器，任务Handler名称

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getEndExecutorHandler() {
        return endExecutorHandler;
    }

    public void setEndExecutorHandler(String endExecutorHandler) {
        this.endExecutorHandler = endExecutorHandler;
    }
}
