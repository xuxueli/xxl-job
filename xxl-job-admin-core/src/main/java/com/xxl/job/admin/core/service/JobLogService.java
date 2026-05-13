package com.xxl.job.admin.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.core.openapi.model.LogResult;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.ibatis.annotations.Param;

/**
 * Job log service interface for xxl-job core module.
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
public interface JobLogService extends IService<XxlJobLog> {

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

    void clearLog(List<Long> ids);

    /**
     * Get log detail cat (with XSS filter)
     * @param logId log id
     * @param fromLineNum from line number
     * @return log result with filtered content
     * @throws XxlException if log not found
     */
    Response<LogResult> getLogDetailCat(long logId, int fromLineNum) throws XxlException;


    List<Long> findClearLogIds(int jobGroup,
                               int jobId,
                               Date clearBeforeTime,
                               int clearBeforeNum,
                               int pagesize);

    /**
     * Find failed job log ids for alarm
     */
    List<Long> findFailJobLogIds(int pagesize);

    /**
     * Update alarm status
     */
    int updateAlarmStatus(long logId, int oldAlarmStatus, int newAlarmStatus);

    /**
     * Find lost job ids (trigger success but not handled, and executor unregistered)
     */
    List<Long> findLostJobIds(Date losedTime);

    /**
     * Save log (compatibility method)
     */
    void saveLog(XxlJobLog xxlJobLog);

    /**
     * Update trigger info
     */
    int updateTriggerInfo(XxlJobLog xxlJobLog);

    /**
     * Update handle info
     */
    int updateHandleInfo(XxlJobLog xxlJobLog);

    /**
     * Delete logs by job id
     */
    int deleteByJobId(int jobId);

    Map<String, Object> findLogReport(@Param("from") Date from, @Param("to") Date to);
}