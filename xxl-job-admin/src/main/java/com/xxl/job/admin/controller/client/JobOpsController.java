package com.xxl.job.admin.controller.client;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Luo Bao Ding
 * @since 2019/5/22
 */
@RestController
@RequestMapping(JobOpsController.JOB_OPS)
public class JobOpsController {
    public static final String JOB_OPS = "/jobops";
    public static final int CODE_PARAM_CONDITION_NOT_SATISFIED = 428;

    @Resource
    private XxlJobInfoDao xxlJobInfoDao;

    @Resource
    private XxlJobService xxlJobService;

    @RequestMapping("/add")
    public ReturnT<String> add(@RequestBody XxlJobInfo jobInfo) {
        return xxlJobService.add(jobInfo);
    }

    @RequestMapping("/update")
    public ReturnT<String> update(@RequestBody XxlJobInfo jobInfo) {
        return xxlJobService.update(jobInfo);
    }

    @RequestMapping("/remove")
    public ReturnT<String> remove(@RequestParam("uniqName") String uniqName) {
        int id = xxlJobInfoDao.findIdByUniqName(uniqName);
        return xxlJobService.remove(id);
    }

    @RequestMapping("/stop")
    public ReturnT<String> stop(@RequestParam("uniqName") String uniqName) {
        int id = xxlJobInfoDao.findIdByUniqName(uniqName);
        return xxlJobService.stop(id);
    }

    @RequestMapping("/start")
    public ReturnT<String> start(@RequestParam("uniqName") String uniqName) {
        int id = xxlJobInfoDao.findIdByUniqName(uniqName);
        return xxlJobService.start(id);
    }

    @RequestMapping("/trigger")
    public ReturnT<String> trigger(@RequestParam("uniqName") String uniqName, @RequestParam("executorParam") String executorParam) {
        if (!StringUtils.hasText(uniqName)) {
            return new ReturnT<>(CODE_PARAM_CONDITION_NOT_SATISFIED, "uniqName '" + uniqName + "' should not be blank");
        }
        int id = xxlJobInfoDao.findIdByUniqName(uniqName);
        if (id <= 0) {
            return new ReturnT<>(CODE_PARAM_CONDITION_NOT_SATISFIED, "uniqName '" + uniqName + "' does not exist");
        }

        if (executorParam == null) {
            executorParam = "";
        }

        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam);

        return ReturnT.SUCCESS;

    }
}
