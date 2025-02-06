package com.xxl.job.admin.controller.api;

import com.xxl.job.admin.controller.JobInfoController;
import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * job api controller
 *
 * @author xingducai
 */
@RestController
@RequestMapping("/api/service/jobinfo")
public class JobInfoApi {
    private static Logger logger = LoggerFactory.getLogger(JobInfoApi.class);

    @Autowired
    JobInfoController jobInfoController;
    @Autowired
    XxlJobService xxlJobService;

    @PermissionLimit(limit = false)
    @RequestMapping("/pageList")
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start, @RequestParam(required = false, defaultValue = "10") int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {

        return jobInfoController.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
    }

    @PermissionLimit(limit = false)
    @PostMapping("/add")
    public ReturnT<String> add(@RequestBody XxlJobInfo jobInfo) {
        return jobInfoController.add(jobInfo);
    }

    @PermissionLimit(limit = false)
    @PostMapping("/addJobRun")
    public ReturnT<String> addJobRun(@RequestBody XxlJobInfo jobInfo) {
        return xxlJobService.addAndStart(jobInfo);

    }

    @PermissionLimit(limit = false)
    @PostMapping("/update")
    public ReturnT<String> update(@RequestBody XxlJobInfo jobInfo) {
        return jobInfoController.update(jobInfo);
    }

    @PermissionLimit(limit = false)
    @DeleteMapping("/remove")
    public ReturnT<String> remove(int id) {
        return jobInfoController.remove(id);
    }

    @PermissionLimit(limit = false)
    @GetMapping("/stop")
    public ReturnT<String> pause(int id) {
        return jobInfoController.pause(id);
    }

    @PermissionLimit(limit = false)
    @GetMapping("/start")
    public ReturnT<String> start(int id) {
        return jobInfoController.start(id);
    }

    @PermissionLimit(limit = false)
    @GetMapping("/trigger")
    public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
        return jobInfoController.triggerJob(id, executorParam, addressList);
    }

    @PermissionLimit(limit = false)
    @GetMapping("/nextTriggerTime")
    public ReturnT<List<String>> nextTriggerTime(String scheduleType, String scheduleConf) {

        return jobInfoController.nextTriggerTime(scheduleType, scheduleConf);

    }

}
