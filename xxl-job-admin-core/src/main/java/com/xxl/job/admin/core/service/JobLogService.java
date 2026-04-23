package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.core.openapi.model.LogResult;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;

import java.util.Map;
import java.util.function.Function;

/**
 * Job log service interface for xxl-job core module.
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
public interface JobLogService {

    /**
     * Page list query for job logs
     */
    PageModel<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, int logStatus, String filterTime);

    /**
     * Load log by id
     */
    XxlJobLog load(long id);

    /**
     * Load log by id, throw XxlException if not found
     * @param id log id
     * @return log object
     * @throws XxlException if log not found
     */
    XxlJobLog loadAndValidate(long id) throws XxlException;

    /**
     * Get log statistics graph data
     */
    Map<String, Object> getLogStatGraph(int jobGroup, int jobId, String fromTime, String toTime);


    Response<String> kill(long id, Function<Integer, Boolean> groupPermissionCheck) throws XxlException;

    /**
     * Clear old logs
     * @param jobGroup job group
     * @param jobId job id
     * @param type clear type (1=1month, 2=3months, 3=6months, 4=1year, 5=1000, 6=10000, 7=30000, 8=100000, 9=all)
     * @return number of logs cleared
     */
    int clearLog(int jobGroup, int jobId, int type);

    /**
     * Get log detail cat (with XSS filter)
     * @param logId log id
     * @param fromLineNum from line number
     * @return log result with filtered content
     * @throws XxlException if log not found
     */
    Response<LogResult> getLogDetailCat(long logId, int fromLineNum) throws XxlException;
}