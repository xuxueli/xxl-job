package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.tool.response.PageModel;

import java.util.Map;

/**
 * Job log service interface for xxl-job core module.
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
public interface JobLogService {

    /**
     * Page list query for job logs
     */
    PageModel<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, int logStatus, String startTime, String endTime);

    /**
     * Load log by id
     */
    XxlJobLog load(long id);

    /**
     * Get log statistics graph data
     */
    Map<String, Object> getLogStatGraph(int jobGroup, int jobId, String fromTime, String toTime);

    /**
     * Kill a running job
     * @return true if kill succeeded
     */
    boolean kill(long id, int userId);

    /**
     * Clear old logs
     * @param jobGroup job group
     * @param jobId job id
     * @param type clear type (1=1month, 2=3months, 3=6months, 4=1year, 5=1000, 6=10000, 7=30000, 8=100000, 9=all)
     * @return number of logs cleared
     */
    int clearLog(int jobGroup, int jobId, int type);
}