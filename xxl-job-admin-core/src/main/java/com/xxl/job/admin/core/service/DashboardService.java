package com.xxl.job.admin.core.service;

import java.util.Date;
import java.util.Map;

/**
 * Dashboard service interface for xxl-job core module.
 * Provides dashboard summary info and chart data.
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface DashboardService {

    /**
     * Get dashboard summary info.
     *
     * @return map with jobInfoCount, jobLogCount, jobLogSuccessCount, executorCount
     */
    Map<String, Object> getDashboardInfo();

    /**
     * Get chart data for specified date range.
     *
     * @param startDate start date
     * @param endDate end date
     * @return map with triggerDayList, triggerDayCount*, triggerCount* statistics
     */
    Map<String, Object> getChartInfo(Date startDate, Date endDate);
}