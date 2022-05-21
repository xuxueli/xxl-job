package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zheng
 */

@RestController
public class InfoNoAuthController {

    @Resource
    private XxlJobService xxlJobService;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;

    @PermissionLimit(limit = false)
    @PostMapping(value = "/info/noAuth/save")
    public ReturnT<String> add(@RequestBody XxlJobInfo jobInfo) {
        int list_count = xxlJobInfoDao.pageListCountByHandlerName(0, 1, jobInfo.getJobGroup(), jobInfo.getExecutorHandler());
        if (list_count == 1){
            return ReturnT.SUCCESS;
        }
        return xxlJobService.add(jobInfo);
    }
}