package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.tool.response.PageModel;

import java.util.List;

public interface JobInfoService {

    /**
     * Add new job
     * @return new job id, 0 if failed
     */
    int add(XxlJobInfo jobInfo, int userId);

    /**
     * Update job
     * @return true if success
     */
    boolean update(XxlJobInfo jobInfo, int userId);

    /**
     * Remove job
     * @return true if success
     */
    boolean remove(int id, int userId);

    /**
     * Start job scheduling
     * @return true if success
     */
    boolean start(int id, int userId);

    /**
     * Stop job scheduling
     * @return true if success
     */
    boolean stop(int id, int userId);

    /**
     * Trigger job manually
     * @return true if success
     */
    boolean trigger(int jobId, int userId, String executorParam, String addressList);

    /**
     * Page list query
     */
    PageModel<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

    /**
     * Generate next trigger times
     * @return list of datetime strings
     */
    List<String> generateNextTriggerTime(XxlJobInfo jobInfo);
}