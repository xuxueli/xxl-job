package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
@RequestMapping("/api")
public class JobApiController2 {

    @Resource
    private AdminBiz adminBiz;

    @RequestMapping("/{uri}")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> api(HttpServletRequest request, @PathVariable("uri") String uri, @RequestBody(required = false) String data) {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri == null || uri.trim().length() == 0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length() > 0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // 提供的服务端交互uri 内部委托AdminBizImpl实现
        switch (uri) {
            case "callback":
                List<HandleCallbackParam> callbackParamList = GsonTool.fromJson(data, List.class, HandleCallbackParam.class);
                return adminBiz.callback(callbackParamList);
            case "registry":
                return adminBiz.registry(GsonTool.fromJson(data, RegistryParam.class));
            case "registryRemove":
                return adminBiz.registryRemove(GsonTool.fromJson(data, RegistryParam.class));
            default:
                return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
        }
    }
}
