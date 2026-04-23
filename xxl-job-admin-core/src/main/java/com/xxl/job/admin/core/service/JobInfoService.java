package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.tool.response.PageModel;

import java.util.List;
import java.util.function.Function;

/**
 * JobInfo service interface
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface JobInfoService {

    /**
     * Page list query
     */
    PageModel<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

    int pageListCount(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

    /**
     * Add new job
     * @param jobInfo job info
     * @param userId operator user id
     * @param userName operator user name
     * @return job id
     * @throws XxlException if validation fails
     */
    int add(XxlJobInfo jobInfo, String userName, Function<Integer, Boolean> groupPermissionCheck) throws XxlException;

    /**
     * Update job
     * @param jobInfo job info
     * @param userId operator user id
     * @param userName operator user name
     * @return 1 if success
     * @throws XxlException if validation fails
     */
    int update(XxlJobInfo jobInfo, String userName, Function<Integer, Boolean> groupPermissionCheck) throws XxlException;

    /**
     * Remove job
     * @param id job id
     * @param userId operator user id
     * @param userName operator user name
     * @return 1 if success
     * @throws XxlException if validation fails
     */
    int remove(int id, String userName, Function<Integer, Boolean> groupPermissionCheck) throws XxlException;

    /**
     * Start job scheduling
     * @param id job id
     * @param userId operator user id
     * @param userName operator user name
     * @return 1 if success
     * @throws XxlException if validation fails
     */
    int start(int id, String userName, Function<Integer, Boolean> groupPermissionCheck) throws XxlException;

    /**
     * Stop job scheduling
     * @param id job id
     * @param userId operator user id
     * @param userName operator user name
     * @return 1 if success
     * @throws XxlException if validation fails
     */
    int stop(int id, String userName, Function<Integer, Boolean> groupPermissionCheck) throws XxlException;

    /**
     * Trigger job manually
     * @param jobId job id
     * @param userId operator user id
     * @param userName operator user name
     * @param executorParam executor param
     * @param addressList address list
     * @return 1 if success
     * @throws XxlException if validation fails
     */
    int trigger(int jobId, String userName, String executorParam, String addressList, Function<Integer, Boolean> groupPermissionCheck) throws XxlException;

    /**
     * Generate next trigger times
     * @param scheduleType schedule type
     * @param scheduleConf schedule config (cron expression or fix rate)
     * @return list of datetime strings
     */
    List<String> generateNextTriggerTime(String scheduleType, String scheduleConf);

    /**
     * Generate next trigger times from job info
     * @param jobInfo job info
     * @return list of datetime strings
     */
    List<String> generateNextTriggerTime(XxlJobInfo jobInfo);

    /**
     * Get job info by id
     */
    XxlJobInfo getJobInfoById(int id);

    /**
     * Get jobs by group id
     */
    List<XxlJobInfo> getJobsByGroupId(int groupId);

    /**
     * Check if user has permission for the job's jobGroup
     * @param userId user id
     * @param jobGroup job group id
     * @return true if has permission
     */
    boolean hasPermission(int userId, int jobGroup);
}