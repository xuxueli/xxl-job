package com.xxl.job.admin.scheduler.openapi;

import com.xxl.job.admin.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.core.openapi.AdminBiz;
import com.xxl.job.core.openapi.model.HandleCallbackRequest;
import com.xxl.job.core.openapi.model.RegistryRequest;
import com.xxl.job.core.openapi.model.ReturnT;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.gson.GsonTool;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
public class OpenApiController {

    @Resource
    private AdminBiz adminBiz;

    /**
     * api
     */
    @RequestMapping("/api/{uri}")
    @ResponseBody
    @XxlSso(login = false)
    public Object api(HttpServletRequest request,
                               @PathVariable("uri") String uri,
                               @RequestHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN) String accesstoken,
                               @RequestBody(required = false) String requestBody) {

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return ReturnT.ofFail("invalid request, HttpMethod not support.");
        }
        if (StringTool.isBlank(uri)) {
            return ReturnT.ofFail("invalid request, uri-mapping empty.");
        }
        if (StringTool.isBlank(requestBody)) {
            return ReturnT.ofFail("invalid request, requestBody empty.");
        }

        // valid token
        if (StringTool.isNotBlank(XxlJobAdminBootstrap.getInstance().getAccessToken())
                && !XxlJobAdminBootstrap.getInstance().getAccessToken().equals(accesstoken)) {
            return ReturnT.ofFail("The access token is wrong.");
        }

        // dispatch request
        try {
            switch (uri) {
                case "callback": {
                    List<HandleCallbackRequest> callbackParamList = GsonTool.fromJson(requestBody, List.class, HandleCallbackRequest.class);
                    return adminBiz.callback(callbackParamList);
                }
                case "registry": {
                    RegistryRequest registryParam = GsonTool.fromJson(requestBody, RegistryRequest.class);
                    return adminBiz.registry(registryParam);
                }
                case "registryRemove": {
                    RegistryRequest registryParam = GsonTool.fromJson(requestBody, RegistryRequest.class);
                    return adminBiz.registryRemove(registryParam);
                    }
                default:
                    return ReturnT.ofFail("invalid request, uri-mapping("+ uri +") not found.");
            }
        } catch (Exception e) {
            return ReturnT.ofFail("openapi invoke error: " + e.getMessage());
        }

    }

}
