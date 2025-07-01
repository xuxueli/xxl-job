package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.bo.XxlJobInfoBo;
import com.xxl.job.admin.service.XxlJobApiService;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * xxl-job api
 *
 * @author: Dao-yang.
 * @date: Created in 2025/6/30 11:07
 */
@Controller
@RequestMapping("/api/job")
public class JobApiController {

    @Resource
    private XxlJobApiService jobApiService;

    @RequestMapping("/add")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> add(@RequestBody XxlJobInfoBo jobInfo) {
        return jobApiService.add(jobInfo);
    }

    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(@RequestParam("id") int id) {
        return jobApiService.remove(id);
    }

    @RequestMapping("/stop")
    @ResponseBody
    public ReturnT<String> pause(@RequestParam("id") int id) {
        return jobApiService.stop(id);
    }

    @RequestMapping("/start")
    @ResponseBody
    public ReturnT<String> start(@RequestParam("id") int id) {
        return jobApiService.start(id);
    }

}
