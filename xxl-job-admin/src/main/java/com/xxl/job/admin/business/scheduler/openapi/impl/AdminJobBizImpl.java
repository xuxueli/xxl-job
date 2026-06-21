package com.xxl.job.admin.business.scheduler.openapi.impl;

import com.xxl.job.admin.business.model.XxlJobInfo;
import com.xxl.job.admin.business.service.XxlJobService;
import com.xxl.job.admin.framework.constant.Consts;
import com.xxl.job.core.openapi.admin.AdminJobBiz;
import com.xxl.job.core.openapi.admin.dto.JobAddRequest;
import com.xxl.job.core.openapi.admin.dto.JobOperateRequest;
import com.xxl.job.core.openapi.admin.dto.JobTriggerRequest;
import com.xxl.job.core.openapi.admin.dto.JobUpdateRequest;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.concurrent.TokenBucket;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.ResponseCode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * openapi admin job service, delegates to XxlJobService with a system-level LoginInfo
 */
@Service
public class AdminJobBizImpl implements AdminJobBiz {

    @Resource
    private XxlJobService xxlJobService;

    /**
     * openapi login info
     */
    private final LoginInfo OPENAPI_LOGIN_INFO = new LoginInfo(
            "0",
            "openapi",
            "OpenAPI",
            null,
            List.of(Consts.ADMIN_ROLE),
            null,
            -1L,
            null);

    /**
     * token bucket, for rate limiting
     */
    private final TokenBucket tokenBucket = TokenBucket.create(100, Duration.ofSeconds(30));

    @Override
    public Response<String> addJob(JobAddRequest request) {

        // rate limit
        if (!tokenBucket.tryAcquire(1, Duration.ofMillis(1000L))) {
            return Response.of(ResponseCode.CODE_502.getCode(), "Too many requests, please try again later.");
        }

        // convert JobAddRequest to XxlJobInfo
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setJobGroup(request.getJobGroup());
        jobInfo.setName(request.getName());
        jobInfo.setAuthor(request.getAuthor());
        jobInfo.setAlarmEmail(request.getAlarmEmail());
        jobInfo.setScheduleType(request.getScheduleType());
        jobInfo.setScheduleConf(request.getScheduleConf());
        jobInfo.setMisfireStrategy(request.getMisfireStrategy());
        jobInfo.setExecutorRouteStrategy(request.getExecutorRouteStrategy());
        jobInfo.setExecutorHandler(request.getExecutorHandler());
        jobInfo.setExecutorParam(request.getExecutorParam());
        jobInfo.setExecutorBlockStrategy(request.getExecutorBlockStrategy());
        jobInfo.setExecutorTimeout(request.getExecutorTimeout());
        jobInfo.setExecutorFailRetryCount(request.getExecutorFailRetryCount());
        jobInfo.setGlueType(request.getGlueType());
        jobInfo.setGlueSource(request.getGlueSource());
        jobInfo.setGlueRemark(request.getGlueRemark());
        jobInfo.setChildJobId(null);

        // add job
        return xxlJobService.add(jobInfo, OPENAPI_LOGIN_INFO);
    }

    @Override
    public Response<String> updateJob(JobUpdateRequest request) {

        // rate limit
        if (!tokenBucket.tryAcquire(1, Duration.ofMillis(1000L))) {
            return Response.of(ResponseCode.CODE_502.getCode(), "Too many requests, please try again later.");
        }

        // convert JobUpdateRequest to XxlJobInfo
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setId(request.getId());
        jobInfo.setJobGroup(0);
        jobInfo.setName(request.getName());
        jobInfo.setAuthor(request.getAuthor());
        jobInfo.setAlarmEmail(request.getAlarmEmail());
        jobInfo.setScheduleType(request.getScheduleType());
        jobInfo.setScheduleConf(request.getScheduleConf());
        jobInfo.setMisfireStrategy(request.getMisfireStrategy());
        jobInfo.setExecutorRouteStrategy(request.getExecutorRouteStrategy());
        jobInfo.setExecutorHandler(request.getExecutorHandler());
        jobInfo.setExecutorParam(request.getExecutorParam());
        jobInfo.setExecutorBlockStrategy(request.getExecutorBlockStrategy());
        jobInfo.setExecutorTimeout(request.getExecutorTimeout());
        jobInfo.setExecutorFailRetryCount(request.getExecutorFailRetryCount());
        jobInfo.setGlueType(request.getGlueType());
        jobInfo.setGlueSource(request.getGlueSource());
        jobInfo.setGlueRemark(request.getGlueRemark());
        jobInfo.setChildJobId(null);

        // update job
        return xxlJobService.update(jobInfo, OPENAPI_LOGIN_INFO);
    }

    @Override
    public Response<String> removeJob(JobOperateRequest request) {

        // rate limit
        if (!tokenBucket.tryAcquire(1, Duration.ofMillis(1000L))) {
            return Response.of(ResponseCode.CODE_502.getCode(), "Too many requests, please try again later.");
        }

        return xxlJobService.remove(request.getId(), OPENAPI_LOGIN_INFO);
    }

    @Override
    public Response<String> startJob(JobOperateRequest request) {

        // rate limit
        if (!tokenBucket.tryAcquire(1, Duration.ofMillis(1000L))) {
            return Response.of(ResponseCode.CODE_502.getCode(), "Too many requests, please try again later.");
        }

        return xxlJobService.start(request.getId(), OPENAPI_LOGIN_INFO);
    }

    @Override
    public Response<String> stopJob(JobOperateRequest request) {

        // rate limit
        if (!tokenBucket.tryAcquire(1, Duration.ofMillis(1000L))) {
            return Response.of(ResponseCode.CODE_502.getCode(), "Too many requests, please try again later.");
        }

        return xxlJobService.stop(request.getId(), OPENAPI_LOGIN_INFO);
    }

    @Override
    public Response<String> triggerJob(JobTriggerRequest request) {

        // rate limit
        if (!tokenBucket.tryAcquire(1, Duration.ofMillis(1000L))) {
            return Response.of(ResponseCode.CODE_502.getCode(), "Too many requests, please try again later.");
        }

        return xxlJobService.trigger(
                OPENAPI_LOGIN_INFO,
                request.getId(),
                request.getExecutorParam(),
                request.getAddressList());
    }

}
