package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermessionLimit;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by Adam on 2018/1/9.
 */
@Controller
@RequestMapping("/internal/jobinfo")
public class InternalJobInfoController {

    @Resource
    private XxlJobService xxlJobService;

    @RequestMapping("/add")
    @ResponseBody
    @PermessionLimit(limit = false)
    public ReturnT<String> add(@RequestBody XxlJobInfo jobInfo) {
        ReturnT<String> t = xxlJobService.addInternal(jobInfo);
        if (t.getCode() == ReturnT.SUCCESS_CODE && jobInfo.getId() > 0)
            xxlJobService.resume(jobInfo.getId());
        return t;
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermessionLimit(limit = false)
    public ReturnT<String> update(@RequestBody XxlJobInfo jobInfo) {
        return xxlJobService.reschedule(jobInfo);
    }

}
