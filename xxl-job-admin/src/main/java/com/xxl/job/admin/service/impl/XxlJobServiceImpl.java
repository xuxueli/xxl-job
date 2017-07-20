package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.core.thread.JobRegistryMonitorHelper;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.admin.dao.IXxlJobLogGlueDao;
import com.xxl.job.admin.service.IXxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.glue.GlueTypeEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * core job action for xxl-job
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class XxlJobServiceImpl implements IXxlJobService {
    private static Logger logger = LoggerFactory.getLogger(XxlJobServiceImpl.class);

    private final IXxlJobGroupDao xxlJobGroupDao;
    private final IXxlJobInfoDao xxlJobInfoDao;
    private final IXxlJobLogDao xxlJobLogDao;
    private final IXxlJobLogGlueDao xxlJobLogGlueDao;
    private final XxlJobDynamicScheduler xxlJobDynamicScheduler;
    private final JobRegistryMonitorHelper jobRegistryMonitorHelper;

    @Override
    public Map<String, Object> pageList(int start, int length, int jobGroup, String executorHandler, String filterTime) {

        // page list
        List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, jobGroup, executorHandler);
        int list_count = xxlJobInfoDao.pageListCount(start, length, jobGroup, executorHandler);

        // fill job info
        if (list != null && list.size() > 0) {
            for (XxlJobInfo jobInfo : list) {
                xxlJobDynamicScheduler.fillJobInfo(jobInfo);
            }
        }

        // package result
        Map<String, Object> maps = new HashMap<>();
        maps.put("recordsTotal", list_count);        // 总记录数
        maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @Override
    public ReturnT<String> add(XxlJobInfo jobInfo) {

        ReturnT<String> checkRet = _checkJobInfo(jobInfo, false);
        if (checkRet.isSuccess()) {
            // add in db
            xxlJobInfoDao.save(jobInfo);
            if (jobInfo.getId() < 1) {
                return ReturnT.error("新增任务失败");
            }

            // add in quartz
            String qz_group = String.valueOf(jobInfo.getJobGroup());
            String qz_name = String.valueOf(jobInfo.getId());
            try {
                xxlJobDynamicScheduler.addJob(qz_name, qz_group, jobInfo.getJobCron());
                //XxlJobDynamicScheduler.pauseJob(qz_name, qz_group);
                return ReturnT.SUCCESS;
            } catch (SchedulerException e) {
                logger.error("", e);
                try {
                    xxlJobInfoDao.delete(jobInfo.getId());
                    xxlJobDynamicScheduler.removeJob(qz_name, qz_group);
                } catch (SchedulerException e1) {
                    logger.error("", e1);
                }
                return ReturnT.error("新增任务失败:" + e.getMessage());
            }
        } else {
            return checkRet;
        }
    }

    @Override
    public ReturnT<String> reschedule(XxlJobInfo jobInfo) {

        ReturnT<String> checkResult = _checkJobInfo(jobInfo, true);
        if (checkResult.isSuccess()) {
            // stage job info
            XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
            if (exists_jobInfo == null) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "参数异常");
            }
            String old_cron = exists_jobInfo.getJobCron();
            exists_jobInfo.merge(jobInfo);
            xxlJobInfoDao.update(exists_jobInfo);

            // fresh quartz
            String qz_group = String.valueOf(exists_jobInfo.getJobGroup());
            String qz_name = String.valueOf(exists_jobInfo.getId());
            try {
                boolean ret = xxlJobDynamicScheduler.rescheduleJob(qz_group, qz_name, exists_jobInfo.getJobCron());
                return ret ? ReturnT.SUCCESS : ReturnT.FAIL;
            } catch (SchedulerException e) {
                logger.error("", e);
            }
            return ReturnT.FAIL;
        } else {
            return checkResult;
        }
    }

    @Override
    public ReturnT<String> remove(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            xxlJobDynamicScheduler.removeJob(name, group);
            xxlJobInfoDao.delete(id);
            xxlJobLogDao.delete(id);
            xxlJobLogGlueDao.deleteByJobId(id);
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return ReturnT.FAIL;
    }

    @Override
    public ReturnT<String> pause(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            boolean ret = xxlJobDynamicScheduler.pauseJob(name, group);    // jobStatus do not store
            return ret ? ReturnT.SUCCESS : ReturnT.FAIL;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @Override
    public ReturnT<String> resume(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            boolean ret = xxlJobDynamicScheduler.resumeJob(name, group);
            return ret ? ReturnT.SUCCESS : ReturnT.FAIL;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @Override
    public ReturnT<String> triggerJob(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            xxlJobDynamicScheduler.triggerJob(name, group);
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @Override
    public Map<String, Object> dashboardInfo() {

        int jobInfoCount = xxlJobInfoDao.findAllCount();
        int jobLogCount = xxlJobLogDao.triggerCountByHandleCode(-1);
        int jobLogSuccessCount = xxlJobLogDao.triggerCountByHandleCode(ReturnT.SUCCESS_CODE);

        // executor count
        Set<String> executorAddressSet = new HashSet<>();
        List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (XxlJobGroup group : groupList) {
                List<String> registryList = null;
                if (group.getAddressType() == 0) {
                    registryList = jobRegistryMonitorHelper.discover(RegistryConfig.RegistType.EXECUTOR.name(), group.getAppName());
                } else {
                    if (StringUtils.isNotBlank(group.getAddressList())) {
                        registryList = Arrays.asList(group.getAddressList().split(","));
                    }
                }
                if (CollectionUtils.isNotEmpty(registryList)) {
                    executorAddressSet.addAll(registryList);
                }
            }
        }
        int executorCount = executorAddressSet.size();

        Map<String, Object> dashboardMap = new HashMap<>();
        dashboardMap.put("jobInfoCount", jobInfoCount);
        dashboardMap.put("jobLogCount", jobLogCount);
        dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
        dashboardMap.put("executorCount", executorCount);
        return dashboardMap;
    }

    @Override
    public ReturnT<Map<String, Object>> triggerChartDate() {
        Date from = DateUtils.addDays(new Date(), -30);
        Date to = new Date();

        List<String> triggerDayList = new ArrayList<>();
        List<Integer> triggerDayCountSucList = new ArrayList<>();
        List<Integer> triggerDayCountFailList = new ArrayList<>();
        int triggerCountSucTotal = 0;
        int triggerCountFailTotal = 0;

        List<Map<String, Object>> triggerCountMapAll = xxlJobLogDao.triggerCountByDay(from, to, -1);
        List<Map<String, Object>> triggerCountMapSuc = xxlJobLogDao.triggerCountByDay(from, to, ReturnT.SUCCESS_CODE);
        if (CollectionUtils.isNotEmpty(triggerCountMapAll)) {
            for (Map<String, Object> item : triggerCountMapAll) {
                String day = String.valueOf(item.get("triggerDay"));
                int dayAllCount = Integer.valueOf(String.valueOf(item.get("triggerCount")));
                int daySucCount = 0;
                int dayFailCount = dayAllCount - daySucCount;

                if (CollectionUtils.isNotEmpty(triggerCountMapSuc)) {
                    for (Map<String, Object> sucItem : triggerCountMapSuc) {
                        String daySuc = String.valueOf(sucItem.get("triggerDay"));
                        if (day.equals(daySuc)) {
                            daySucCount = Integer.valueOf(String.valueOf(sucItem.get("triggerCount")));
                            dayFailCount = dayAllCount - daySucCount;
                        }
                    }
                }

                triggerDayList.add(day);
                triggerDayCountSucList.add(daySucCount);
                triggerDayCountFailList.add(dayFailCount);
                triggerCountSucTotal += daySucCount;
                triggerCountFailTotal += dayFailCount;
            }
        } else {
            for (int i = 4; i > -1; i--) {
                triggerDayList.add(FastDateFormat.getInstance("yyyy-MM-dd").format(DateUtils.addDays(new Date(), -i)));
                triggerDayCountSucList.add(0);
                triggerDayCountFailList.add(0);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("triggerDayList", triggerDayList);
        result.put("triggerDayCountSucList", triggerDayCountSucList);
        result.put("triggerDayCountFailList", triggerDayCountFailList);
        result.put("triggerCountSucTotal", triggerCountSucTotal);
        result.put("triggerCountFailTotal", triggerCountFailTotal);
        return new ReturnT<>(result);
    }


    private ReturnT<String> _checkJobInfo(XxlJobInfo jobInfo, boolean isUpdate) {
// valid
        XxlJobGroup group = xxlJobGroupDao.load(jobInfo.getJobGroup());
        if (!isUpdate && group == null) {
            return ReturnT.error("请选择“执行器”");
        }
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return ReturnT.error("请输入格式正确的“Cron”");
        }
        if (StringUtils.isBlank(jobInfo.getJobDesc())) {
            return ReturnT.error("请输入“任务描述”");
        }
        if (StringUtils.isBlank(jobInfo.getAuthor())) {
            return ReturnT.error("请输入“负责人”");
        }
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return ReturnT.error("路由策略非法");
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return ReturnT.error("阻塞处理策略非法");
        }
        if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
            return ReturnT.error("失败处理策略非法");
        }
        if (!isUpdate && GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
            return ReturnT.error("运行模式非法非法");
        }
        if (!isUpdate && GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
            return ReturnT.error("请输入“JobHandler”");
        }

        // fix "\r" in shell
        if (!isUpdate && GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null) {
            jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
        }

        // childJobKey valid
        if (StringUtils.isNotBlank(jobInfo.getChildJobKey())) {
            String[] childJobKeys = jobInfo.getChildJobKey().split(",");
            for (String childJobKeyItem : childJobKeys) {
                String[] childJobKeyArr = childJobKeyItem.split("_");
                if (childJobKeyArr.length != 2) {
                    return ReturnT.error(MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
                }
                XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.valueOf(childJobKeyArr[1]));
                if (childJobInfo == null) {
                    return ReturnT.error(MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
                }
            }
        }
        return ReturnT.success("验证通过");
    }

}
