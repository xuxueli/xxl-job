package com.xxl.job.admin.common.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务日志报告VO
 *
 * @author Rong.Jia
 * @date 2023/05/14
 */
@Data
public class JobLogReportVO implements Serializable {

    private static final long serialVersionUID = -3573468652178196044L;

    /**
     * 触发数量
     */
    private Long triggerDayCount;

    /**
     * 触发正在运行的数量
     */
    private Long triggerDayCountRunning;

    /**
     * 触发成功的数量
     */
    private Long triggerDayCountSuc;

    public Long getTriggerDayCount() {
        return triggerDayCount == null ? 0L : triggerDayCount;
    }

    public Long getTriggerDayCountRunning() {
        return triggerDayCountRunning == null ? 0L : triggerDayCountRunning;
    }

    public Long getTriggerDayCountSuc() {
        return triggerDayCountSuc == null ? 0L : triggerDayCountSuc;
    }
}
