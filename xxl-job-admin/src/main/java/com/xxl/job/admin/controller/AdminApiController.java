package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
@RequestMapping("/api")
public class AdminApiController {

    @Resource
    private AdminBiz adminBiz;

    @RequestMapping("/callback")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> callback(@RequestBody(required = false) List<HandleCallbackParam> data) {
        return adminBiz.callback(data);

    }

    @RequestMapping("/registry")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> registry(@RequestBody(required = false) RegistryParam data) {
        return adminBiz.registry(data);
    }

    @RequestMapping("/registryRemove")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> registryRemove(@RequestBody(required = false) RegistryParam data) {
        return adminBiz.registryRemove(data);
    }

}
