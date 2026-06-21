package com.xxl.job.core.openapi.admin;

import com.xxl.job.core.openapi.admin.dto.JobAddRequest;
import com.xxl.job.core.openapi.admin.dto.JobOperateRequest;
import com.xxl.job.core.openapi.admin.dto.JobTriggerRequest;
import com.xxl.job.core.openapi.admin.dto.JobUpdateRequest;
import com.xxl.tool.response.Response;

/**
 * openapi admin job interface for job lifecycle management
 *
 * @author xuxueli
 */
public interface AdminJobBiz {

    /**
     * add a new job
     */
    Response<String> addJob(JobAddRequest request);

    /**
     * update existing job config
     */
    Response<String> updateJob(JobUpdateRequest request);

    /**
     * remove a job by id
     */
    Response<String> removeJob(JobOperateRequest request);

    /**
     * start / enable a scheduled job
     */
    Response<String> startJob(JobOperateRequest request);

    /**
     * stop / disable a scheduled job
     */
    Response<String> stopJob(JobOperateRequest request);

    /**
     * trigger a job manually once
     */
    Response<String> triggerJob(JobTriggerRequest request);

}
