package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: fengfaling
 * @create 2021-05-22 12:07
 */

@Controller
@RequestMapping("/api/xxlJob")
public class JobInfoApiController {

    private static Logger logger = LoggerFactory.getLogger(JobInfoApiController.class);

    @Resource
    private XxlJobService xxlJobService;

    @ResponseBody
    @RequestMapping("/add")
    @PermissionLimit(limit = false)
    public ReturnT<String> add(HttpServletRequest request, @RequestBody XxlJobInfo jobInfo) {
        ReturnT checkResult = validAccessToken(request);
        if (checkResult != null) {
            return checkResult;
        }
        return xxlJobService.add(jobInfo);
    }

    @ResponseBody
    @RequestMapping("/update")
    @PermissionLimit(limit = false)
    public ReturnT<String> update(HttpServletRequest request, @RequestBody XxlJobInfo jobInfo) {
        ReturnT checkResult = validAccessToken(request);
        if (checkResult != null) {
            return checkResult;
        }
        return xxlJobService.update(jobInfo);
    }

    @ResponseBody
    @RequestMapping("/stop")
    @PermissionLimit(limit = false)
    public ReturnT<String> pause(HttpServletRequest request, @RequestBody Integer jobId) {
        ReturnT checkResult = validAccessToken(request);
        if (checkResult != null) {
            return checkResult;
        }
        return xxlJobService.stop(jobId);
    }

    @ResponseBody
    @RequestMapping("/start")
    @PermissionLimit(limit = false)
    public ReturnT<String> start(HttpServletRequest request, @RequestBody Integer jobId) {
        ReturnT checkResult = validAccessToken(request);
        if (checkResult != null) {
            return checkResult;
        }
        return xxlJobService.start(jobId);
    }

    @ResponseBody
    @RequestMapping("/remove")
    @PermissionLimit(limit = false)
    public ReturnT<String> remove(HttpServletRequest request, @RequestBody Integer jobId) {
        ReturnT checkResult = validAccessToken(request);
        if (checkResult != null) {
            return checkResult;
        }
        return xxlJobService.remove(jobId);
    }

    public ReturnT validAccessToken(HttpServletRequest request) {
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken() != null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length() > 0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }
        return null;
    }
}
