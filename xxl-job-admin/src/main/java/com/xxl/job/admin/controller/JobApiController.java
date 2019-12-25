package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.exception.XxlJobException;
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


    // ---------------------- base ----------------------

    /**
     * valid access token
     */
    private void validAccessToken(HttpServletRequest request){
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken()!=null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length()>0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_RPC_ACCESS_TOKEN))) {
            throw new XxlJobException("The access token is wrong.");
        }
    }

    /**
     * parse Param
     */
    private Object parseParam(String data, Class<?> parametrized, Class<?>... parameterClasses){
        Object param = null;
        try {
            if (parameterClasses != null) {
                param = JacksonUtil.readValue(data, parametrized, parameterClasses);
            } else {
                param = JacksonUtil.readValue(data, parametrized);
            }
        } catch (Exception e) { }
        if (param==null) {
            throw new XxlJobException("The request data invalid.");
        }
        return param;
    }

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
        validAccessToken(request);

        // param
        List<HandleCallbackParam> callbackParamList = (List<HandleCallbackParam>) parseParam(data, List.class, HandleCallbackParam.class);

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
        validAccessToken(request);

        // param
        RegistryParam registryParam = (RegistryParam) parseParam(data, RegistryParam.class);

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
        validAccessToken(request);

        // param
        RegistryParam registryParam = (RegistryParam) parseParam(data, RegistryParam.class);

        // invoke
        return adminBiz.registryRemove(registryParam);
    }

    // ---------------------- job biz ----------------------

}
