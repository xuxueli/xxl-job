package com.xxl.job.admin.business.scheduler.openapi;

import com.xxl.job.admin.business.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.business.model.XxlJobGroup;
import com.xxl.job.admin.business.model.XxlJobInfo;
import com.xxl.job.admin.business.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.core.constant.Const;
import com.xxl.job.core.openapi.admin.AdminBiz;
import com.xxl.job.core.openapi.admin.AdminJobBiz;
import com.xxl.job.core.openapi.admin.dto.CallbackRequest;
import com.xxl.job.core.openapi.admin.dto.JobAddRequest;
import com.xxl.job.core.openapi.admin.dto.JobOperateRequest;
import com.xxl.job.core.openapi.admin.dto.JobTriggerRequest;
import com.xxl.job.core.openapi.admin.dto.JobUpdateRequest;
import com.xxl.job.core.openapi.admin.dto.RegistryRequest;
import com.xxl.job.admin.framework.util.I18nUtil;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
public class OpenApiController {

    @Resource
    private AdminBiz adminBiz;

    @Resource
    private AdminJobBiz adminJobBiz;

    @Resource
    private XxlJobInfoMapper xxlJobInfoMapper;

    /**
     * api
     */
    @RequestMapping("/api/{uri}")
    @ResponseBody
    @XxlSso(login = false)
    public Object api(HttpServletRequest request,
                      @PathVariable("uri") String uri,
                      @RequestHeader(value = Const.XXL_JOB_ACCESS_TOKEN, required = false) String accessToken,
                      @RequestHeader(value = Const.XXL_JOB_APPNAME, required = false) String appname,
                      @RequestBody(required = false) String requestBody) {

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return Response.ofFail("invalid request, HttpMethod not support.");
        }
        if (StringTool.isBlank(uri)) {
            return Response.ofFail("invalid request, uri-mapping empty.");
        }
        if (StringTool.isBlank(requestBody)) {
            return Response.ofFail("invalid request, requestBody empty.");
        }

        // valid: appname + accessToken
        if (StringTool.isBlank(accessToken) || StringTool.isBlank(appname)) {
            return Response.ofFail("invalid request, accessToken or appname is empty.");
        }
        XxlJobGroup xxlJobGroup = XxlJobAdminBootstrap.getInstance().getJobRegistryHelper().loadByAppName(appname);
        if (!(xxlJobGroup!=null && accessToken.equals(xxlJobGroup.getAccessToken()))) {
            return Response.ofFail("invalid request, accessToken or appname invalid.");
        }

        // dispatch request
        try {
            switch (uri) {
                case "callback": {
                    CallbackRequest callbackParam = GsonTool.fromJson(requestBody, CallbackRequest.class);
                    return adminBiz.callback(callbackParam);
                }
                case "registry": {
                    RegistryRequest registryParam = GsonTool.fromJson(requestBody, RegistryRequest.class);
                    return adminBiz.registry(registryParam);
                }
                case "registryRemove": {
                    RegistryRequest registryParam = GsonTool.fromJson(requestBody, RegistryRequest.class);
                    return adminBiz.registryRemove(registryParam);
                    }
                case "addJob": {
                    JobAddRequest jobParam = GsonTool.fromJson(requestBody, JobAddRequest.class);
                    if (jobParam.getJobGroup() != xxlJobGroup.getId()) {
                        return permissionDenied();
                    }
                    return adminJobBiz.addJob(jobParam);
                }
                case "updateJob": {
                    JobUpdateRequest jobParam = GsonTool.fromJson(requestBody, JobUpdateRequest.class);
                    if (!hasJobPermission(xxlJobGroup, jobParam.getId())) {
                        return permissionDenied();
                    }
                    return adminJobBiz.updateJob(jobParam);
                }
                case "removeJob": {
                    JobOperateRequest jobParam = GsonTool.fromJson(requestBody, JobOperateRequest.class);
                    if (!hasJobPermission(xxlJobGroup, jobParam.getId())) {
                        return permissionDenied();
                    }
                    return adminJobBiz.removeJob(jobParam);
                }
                case "startJob": {
                    JobOperateRequest jobParam = GsonTool.fromJson(requestBody, JobOperateRequest.class);
                    if (!hasJobPermission(xxlJobGroup, jobParam.getId())) {
                        return permissionDenied();
                    }
                    return adminJobBiz.startJob(jobParam);
                }
                case "stopJob": {
                    JobOperateRequest jobParam = GsonTool.fromJson(requestBody, JobOperateRequest.class);
                    if (!hasJobPermission(xxlJobGroup, jobParam.getId())) {
                        return permissionDenied();
                    }
                    return adminJobBiz.stopJob(jobParam);
                }
                case "triggerJob": {
                    JobTriggerRequest jobParam = GsonTool.fromJson(requestBody, JobTriggerRequest.class);
                    if (!hasJobPermission(xxlJobGroup, jobParam.getId())) {
                        return permissionDenied();
                    }
                    return adminJobBiz.triggerJob(jobParam);
                }
                default:
                    return Response.ofFail("invalid request, uri-mapping("+ uri +") not found.");
            }
        } catch (Exception e) {
            return Response.ofFail("openapi invoke error: " + e.getMessage());
        }

    }

    private boolean hasJobPermission(XxlJobGroup authenticatedGroup, int jobId) {
        XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(jobId);
        return jobInfo != null && jobInfo.getJobGroup() == authenticatedGroup.getId();
    }

    private Response<String> permissionDenied() {
        return Response.ofFail(I18nUtil.getString("system_permission_limit"));
    }

}
