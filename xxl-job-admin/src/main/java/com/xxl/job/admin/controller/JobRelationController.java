package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.XxlJobService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
@Controller
@RequestMapping
public class JobRelationController {
    @Resource
    private XxlJobService xxlJobService;

    @PermissionLimit(limit = false)
    @ResponseBody
    @RequestMapping(value = "/jobrelation/findAllRelationById")
    public Map<String, XxlJobInfo> findAllRelationById(@RequestParam(required = false, defaultValue = "") String jobId) {
        XxlJobInfo relation = xxlJobService.findAllRelationById(Integer.parseInt(jobId));
        Map<String, XxlJobInfo> maps = new HashMap<>(2);
        // 总记录数
        // 所有记录列表
        maps.put("data", relation);
        return maps;
    }

}
