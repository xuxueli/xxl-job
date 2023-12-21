package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerSimpleParam;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
@RequestMapping("/api")
public class JobApiController {

    @Resource
    private AdminBiz adminBiz;

    @Resource
    private XxlJobService xxlJobService;

    /**
     * api
     *
     * @param uri
     * @param data
     * @return
     */
    @RequestMapping("/{uri}")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> api(HttpServletRequest request, @PathVariable("uri") String uri, @RequestBody(required = false) String data) {

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri == null || uri.trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length() > 0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // services mapping
        if ("callback".equals(uri)) {
            List<HandleCallbackParam> callbackParamList = GsonTool.fromJson(data, List.class, HandleCallbackParam.class);
            return adminBiz.callback(callbackParamList);
        } else if ("registry".equals(uri)) {
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            return adminBiz.registry(registryParam);
        } else if ("registryRemove".equals(uri)) {
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            return adminBiz.registryRemove(registryParam);
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
        }

    }

    @PostMapping("/job/trigger")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> triggerJob(HttpServletRequest request, @RequestBody(required = false) String data) {
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().isEmpty()
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }
        TriggerSimpleParam triggerParam = GsonTool.fromJson(data, TriggerSimpleParam.class);
        // force cover job param
        if (triggerParam.getExecutorParams() == null) {
            triggerParam.setExecutorParams("");
        }

        JobTriggerPoolHelper.trigger(triggerParam.getJobId(), TriggerTypeEnum.MANUAL, -1, null, triggerParam.getExecutorParams(), null);
        return ReturnT.SUCCESS;
    }

    @PostMapping("/job/add")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> addJob(HttpServletRequest request, @RequestBody(required = false) String data) {
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().isEmpty()
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }
        XxlJobInfo jobInfo = GsonTool.fromJson(data, XxlJobInfo.class);
        return xxlJobService.add(jobInfo);
    }

    @PostMapping("/job/update")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> updateJob(HttpServletRequest request, @RequestBody(required = false) String data) {
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().isEmpty()
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }
        XxlJobInfo jobInfo = GsonTool.fromJson(data, XxlJobInfo.class);
        return xxlJobService.update(jobInfo);
    }

    @PostMapping("/job/remove/{id}")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> removeJob(HttpServletRequest request, @PathVariable("id") int id) {
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().isEmpty()
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }
        return xxlJobService.remove(id);
    }

    @PostMapping("/job/stop/{id}")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> stopJob(HttpServletRequest request, @PathVariable("id") int id) {
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().isEmpty()
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }
        return xxlJobService.stop(id);
    }

    @PostMapping("/job/start/{id}")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> startJob(HttpServletRequest request, @PathVariable("id") int id) {
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().isEmpty()
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }
        return xxlJobService.start(id);
    }
}
