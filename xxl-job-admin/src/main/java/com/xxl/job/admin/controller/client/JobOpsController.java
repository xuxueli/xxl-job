package com.xxl.job.admin.controller.client;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Luo Bao Ding
 * @since 2019/5/22
 */
@RestController
@RequestMapping(JobOpsController.JOB_OPS)
public class JobOpsController {
    public static final String JOB_OPS = "/jobops";
    public static final int PARAM_CONDITION_NOT_SATISFIED = 428;

    @Resource
    private XxlJobInfoDao xxlJobInfoDao;

    @Resource
    private XxlJobService xxlJobService;

    @RequestMapping("/add")
    public ReturnT<String> add(XxlJobInfo jobInfo) {
        return xxlJobService.add(jobInfo);
    }

    @RequestMapping("/update")
    public ReturnT<String> update(XxlJobInfo jobInfo) {
        return xxlJobService.update(jobInfo);
    }

    @RequestMapping("/remove")
    public ReturnT<String> remove(int id) {
        return xxlJobService.remove(id);
    }

    @RequestMapping("/stop")
    public ReturnT<String> pause(int id) {
        return xxlJobService.stop(id);
    }

    @RequestMapping("/start")
    public ReturnT<String> start(int id) {
        return xxlJobService.start(id);
    }

    @RequestMapping("/trigger")
    public ReturnT<String> triggerJob(int id, String executorParam) {
        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }

        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/triggerByUniqName")
    public ReturnT<String> triggerByUniqName(@RequestParam("uniqName") String uniqName, @RequestParam("executorParam") String executorParam) {
        if (!StringUtils.hasText(uniqName)) {
            return new ReturnT<>(PARAM_CONDITION_NOT_SATISFIED, "uniqName '" + uniqName + "' should not be blank");
        }
        List<Integer> ids = xxlJobInfoDao.findIdByUniqName(uniqName);
        if (ids == null || ids.size() == 0) {
            return new ReturnT<>(PARAM_CONDITION_NOT_SATISFIED, "uniqName '" + uniqName + "' does not exist");
        }
        int id = ids.get(0);

        if (executorParam == null) {
            executorParam = "";
        }

        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam);

        return ReturnT.SUCCESS;

    }
}
