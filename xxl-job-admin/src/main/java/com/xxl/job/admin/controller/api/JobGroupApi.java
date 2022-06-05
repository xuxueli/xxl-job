package com.xxl.job.admin.controller.api;

import com.xxl.job.admin.controller.JobGroupController;
import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * job group api controller
 *
 * @author xingducai
 */
@RestController
@RequestMapping("/api/service/jobGroup")
public class JobGroupApi {

    @Autowired
    JobGroupController jobGroupController;

    @PermissionLimit(limit = false)
    @GetMapping("/pageList")
    public Map<String, Object> pageList(HttpServletRequest request, @RequestParam(required = false, defaultValue = "0")
    int start, @RequestParam(required = false, defaultValue = "10") int length, String appname, String title) {

        return jobGroupController.pageList(request, start, length, appname, title);
    }

    @PermissionLimit(limit = false)
    @PostMapping("/save")
    public ReturnT<String> save(@RequestBody XxlJobGroup xxlJobGroup) {
        return jobGroupController.save(xxlJobGroup);
    }

    @PermissionLimit(limit = false)
    @PostMapping("/update")
    public ReturnT<String> update(@RequestBody XxlJobGroup xxlJobGroup) {
        return jobGroupController.update(xxlJobGroup);
    }

    @PermissionLimit(limit = false)
    @DeleteMapping("/remove")
    public ReturnT<String> remove(@RequestParam("id") Integer id) {
        return jobGroupController.remove(id);
    }

    @PermissionLimit(limit = false)
    @GetMapping("/loadById")
    public ReturnT<XxlJobGroup> loadById(@RequestParam("id") Integer id) {
        return jobGroupController.loadById(id);
    }

}
