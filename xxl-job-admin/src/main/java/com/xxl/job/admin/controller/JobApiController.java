package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.springframework.stereotype.Controller;
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
public class JobApiController {

    @Resource
    private AdminBiz adminBiz;


    // ---------------------- admin biz ----------------------

    /**
     * callback
     *
     * @param data
     * @return
     */
    @RequestMapping("/callback")
    @ResponseBody
    @PermissionLimit(limit=false)
    public ReturnT<String> callback(HttpServletRequest request, @RequestBody(required = false) String data) {
        // valid
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken()!=null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length()>0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_RPC_ACCESS_TOKEN))) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // param
        List<HandleCallbackParam> callbackParamList = null;
        try {
            callbackParamList = JacksonUtil.readValue(data, List.class, HandleCallbackParam.class);
        } catch (Exception e) { }
        if (callbackParamList==null || callbackParamList.size()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The request data invalid.");
        }

        // invoke
        return adminBiz.callback(callbackParamList);
    }



    /**
     * registry
     *
     * @param data
     * @return
     */
    @RequestMapping("/registry")
    @ResponseBody
    @PermissionLimit(limit=false)
    public ReturnT<String> registry(HttpServletRequest request, @RequestBody(required = false) String data) {
        // valid
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken()!=null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length()>0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_RPC_ACCESS_TOKEN))) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // param
        RegistryParam registryParam = null;
        try {
            registryParam = JacksonUtil.readValue(data, RegistryParam.class);
        } catch (Exception e) {}
        if (registryParam == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The request data invalid.");
        }

        // invoke
        return adminBiz.registry(registryParam);
    }

    /**
     * registry remove
     *
     * @param data
     * @return
     */
    @RequestMapping("/registryRemove")
    @ResponseBody
    @PermissionLimit(limit=false)
    public ReturnT<String> registryRemove(HttpServletRequest request, @RequestBody(required = false) String data) {
        // valid
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken()!=null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length()>0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_RPC_ACCESS_TOKEN))) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // param
        RegistryParam registryParam = null;
        try {
            registryParam = JacksonUtil.readValue(data, RegistryParam.class);
        } catch (Exception e) {}
        if (registryParam == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The request data invalid.");
        }

        // invoke
        return adminBiz.registryRemove(registryParam);
    }

    // ---------------------- job biz ----------------------

}
