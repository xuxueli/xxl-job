package com.xxl.job.admin.platform.report.data;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ice2Faith
 * @date 2025/10/11 19:44
 * @desc
 */
@Data
@NoArgsConstructor
public class LogReportDto {
    protected int triggerDayCount;
    protected int triggerDayCountRunning;
    protected int triggerDayCountSuc;
}
