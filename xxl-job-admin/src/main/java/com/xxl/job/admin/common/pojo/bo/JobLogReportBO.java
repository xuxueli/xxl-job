package com.xxl.job.admin.common.pojo.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 日志报告BO
 *
 * @author Rong.Jia
 * @date 2023/05/14
 */
@Data
public class JobLogReportBO implements Serializable {

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

    /**
     * 触发失败的数量
     */
    private Long triggerDayCountFail;

}
