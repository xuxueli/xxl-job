package com.xxl.job.admin.controller.rest;

import com.xxl.job.admin.controller.JobInfoController;
import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.Xxl;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.KillParam;
import com.xxl.job.core.biz.model.LogParam;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Tag(name = "日志管理")
@Controller(value = "restJobLogController")
@RequestMapping("/rest/jobLogs")
public class JobLogController {
    private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobLogDao xxlJobLogDao;


//    @RequestMapping("/getJobsByGroup")
//    @ResponseBody
//    public ReturnT<List<XxlJobInfo>> getJobsByGroup(int jobGroup) {
//        List<XxlJobInfo> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
//        return new ReturnT<List<XxlJobInfo>>(list);
//    }

    @GetMapping("")
    @ResponseBody
    public ReturnT<PageInfo<Xxl>> list(HttpServletRequest request,
                                       @RequestParam(required = false, defaultValue = "1") int current,
                                       @RequestParam(required = false, defaultValue = "10") int pageSize,
                                       @RequestParam(required = false, defaultValue = "0") int jobGroup,
                                       @RequestParam(required = false, defaultValue = "0") int jobId,
                                       @RequestParam(required = false, defaultValue = "-1") int logStatus,
                                       String[] filterTime) {

        int start = (current - 1) * pageSize;
        // valid permission
        JobInfoController.validPermission(request, jobGroup);    // 仅管理员支持查询全部；普通用户仅支持查询有权限的 jobGroup

        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
//        if (filterTime != null && filterTime.trim().length() > 0) {
//            String[] temp = filterTime.split(" - ");
//            if (temp.length == 2) {
//                triggerTimeStart = DateUtil.parseDateTime(temp[0]);
//                triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
//            }
//        }
        if (filterTime != null && filterTime.length == 2) {
            triggerTimeStart = DateUtil.parseDateTime(filterTime[0]);
            triggerTimeEnd = DateUtil.parseDateTime(filterTime[1]);
        }

        // page query
        List<Xxl> list = xxlJobLogDao.pageList(start, pageSize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        int listCount = xxlJobLogDao.pageListCount(start, pageSize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

        final PageInfo<Xxl> pageInfo = new PageInfo<>(list, listCount, current, pageSize);
        final ReturnT<PageInfo<Xxl>> ret = new ReturnT<>(pageInfo);

        return ret;
    }

    /**
     * 获取日志
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ReturnT<Xxl> get(@PathVariable int id) {
        Xxl jobLog = xxlJobLogDao.load(id);
        if (jobLog == null) {
            throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
        }
        return new ReturnT<Xxl>(jobLog);
    }

    @RequestMapping("/logDetailCat")
    @ResponseBody
    public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, long logId, int fromLineNum) {
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(executorAddress);
            ReturnT<LogResult> logResult = executorBiz.log(new LogParam(triggerTime, logId, fromLineNum));

            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                Xxl jobLog = xxlJobLogDao.load(logId);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            return logResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    /**
     * 通过id删除日志
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ReturnT<String> delete(@PathVariable int id) {
        // base check
        Xxl log = xxlJobLogDao.load(id);
        XxlJobInfo jobInfo = xxlJobInfoDao.loadById(log.getJobId());
        if (jobInfo == null) {
            return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
            return new ReturnT<String>(500, I18nUtil.getString("joblog_kill_log_limit"));
        }

        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(log.getExecutorAddress());
            runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            runResult = new ReturnT<String>(500, e.getMessage());
        }

        if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
            log.setHandleCode(ReturnT.FAIL_CODE);
            log.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
            log.setHandleTime(new Date());
            XxlJobCompleter.updateHandleInfoAndFinish(log);
            return new ReturnT<String>(runResult.getMsg());
        } else {
            return new ReturnT<String>(500, runResult.getMsg());
        }
    }

    @DeleteMapping("")
    @ResponseBody
    public ReturnT<String> deleteByCondition(@RequestParam int jobGroup, @RequestParam int jobId, @RequestParam int type) {

        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -1);    // 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -3);    // 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -6);    // 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtil.addYears(new Date(), -1);    // 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000;        // 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000;        // 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000;        // 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000;    // 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0;            // 清理所有日志数据
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("joblog_clean_type_unvalid"));
        }

        List<Long> logIds = null;
        do {
            logIds = xxlJobLogDao.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && logIds.size() > 0) {
                xxlJobLogDao.clearLog(logIds);
            }
        } while (logIds != null && logIds.size() > 0);

        return ReturnT.SUCCESS;
    }

}
