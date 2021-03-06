package com.xxl.job.admin.controller.rest;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.Xxl;
import com.xxl.job.admin.core.thread.JobScheduleHelper;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 * @author xuxueli 2015-12-19 16:13:16
 */
@Tag(name = "任务管理")
@Controller(value = "restJobController")
@RequestMapping("/rest/jobs")
public class JobInfoController {
    private static Logger logger = LoggerFactory.getLogger(JobInfoController.class);

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobService xxlJobService;


    @GetMapping("")
    @ResponseBody
    public ReturnT<PageInfo<XxlJobInfo>> list(@RequestParam(required = false, defaultValue = "1") int current,
                                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                                              @RequestParam(required = false, defaultValue = "0") int jobGroup,
                                              @RequestParam(required = false, defaultValue = "-1") int triggerStatus,
                                              @RequestParam(required = false, defaultValue = "") String jobDesc,
                                              @RequestParam(required = false, defaultValue = "") String executorHandler,
                                              @RequestParam(required = false, defaultValue = "") String author) {
        int start = (current - 1) * pageSize;
        // page list
        List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, pageSize, jobGroup, triggerStatus, jobDesc, executorHandler, author);
        int listCount = xxlJobInfoDao.pageListCount(start, pageSize, jobGroup, triggerStatus, jobDesc, executorHandler, author);

        final PageInfo<XxlJobInfo> pageInfo = new PageInfo<>(list, listCount, current, pageSize);
        final ReturnT<PageInfo<XxlJobInfo>> ret = new ReturnT<>(pageInfo);

        return ret;
//        // package result
//        Map<String, Object> maps = new HashMap<String, Object>();
//        maps.put("recordsTotal", listCount);		// 总记录数
//        maps.put("recordsFiltered", listCount);	// 过滤后的总记录数
//        maps.put("data", list);  					// 分页列表
//        return maps;
//        return xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
    }

    /**
     * 不带分页信息的
     *
     * @param jobGroup
     * @return
     */
    @GetMapping("/all")
    @ResponseBody
    public ReturnT<List<XxlJobInfo>> listByGroup(int jobGroup) {
        // page list
        final List<XxlJobInfo> jobsByGroup = xxlJobInfoDao.getJobsByGroup(jobGroup);
        return new ReturnT<>(jobsByGroup);
    }

    @PostMapping("")
    @ResponseBody
    public ReturnT<String> create(@RequestBody XxlJobInfo jobInfo) {
        return xxlJobService.add(jobInfo);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ReturnT<String> update(@PathVariable int id, @RequestBody XxlJobInfo jobInfo) {
        jobInfo.setId(id);
        return xxlJobService.update(jobInfo);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ReturnT<String> delete(@PathVariable int id) {
        return xxlJobService.remove(id);
    }

    @PostMapping("/{id}/stop")
    @ResponseBody
    public ReturnT<String> pause(@PathVariable int id) {
        return xxlJobService.stop(id);
    }

    @PostMapping("/{id}/start")
    @ResponseBody
    public ReturnT<String> start(@PathVariable int id) {
        return xxlJobService.start(id);
    }

    @PostMapping("/{id}/trigger")
    @ResponseBody
    //@PermissionLimit(limit = false)
    public ReturnT<String> triggerJob(@PathVariable int id, @RequestBody TriggerOnceInfo triggerOnceInfo) {
        // force cover job param
        if (triggerOnceInfo.getExecutorParam() == null) {
            triggerOnceInfo.setExecutorParam("");
        }

        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null,
            triggerOnceInfo.getExecutorParam(), triggerOnceInfo.getAddressList()
        );
        return ReturnT.SUCCESS;
    }

    @GetMapping("/nextTriggerTimes")
    @ResponseBody
    public ReturnT<List<String>> nextTriggerTime(String scheduleType, String scheduleConf) {

        XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
        paramXxlJobInfo.setScheduleType(scheduleType);
        paramXxlJobInfo.setScheduleConf(scheduleConf);

        List<String> result = new ArrayList<>();
        try {
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = JobScheduleHelper.generateNextValidTime(paramXxlJobInfo, lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<List<String>>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")) + e.getMessage());
        }
        return new ReturnT<List<String>>(result);

    }

}
