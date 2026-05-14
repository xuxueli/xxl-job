package com.xxl.job.admin.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Date;

/**
 * xxl-job log report
 *
 * @author xuxueli  2019-11-22
 */
@TableName("xxl_job_log_report")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XxlJobLogReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID - 自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 触发日期
     */
    private Date triggerDay;

    /**
     * 运行中数量
     */
    private int runningCount;

    /**
     * 成功数量
     */
    private int sucCount;

    /**
     * 失败数量
     */
    private int failCount;

    /**
     * 更新时间 - 自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Date getTriggerDay() { return triggerDay; }
    public void setTriggerDay(Date triggerDay) { this.triggerDay = triggerDay; }

    public int getRunningCount() { return runningCount; }
    public void setRunningCount(int runningCount) { this.runningCount = runningCount; }

    public int getSucCount() { return sucCount; }
    public void setSucCount(int sucCount) { this.sucCount = sucCount; }

    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}