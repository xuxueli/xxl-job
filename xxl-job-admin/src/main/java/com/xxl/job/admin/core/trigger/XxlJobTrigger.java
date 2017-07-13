package com.xxl.job.admin.core.trigger;

import com.xxl.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.core.thread.JobFailMonitorHelper;
import com.xxl.job.admin.core.thread.JobRegistryMonitorHelper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.enums.RegistryConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * xxl-job trigger
 * Created by xuxueli on 17/7/13.
 */
public class XxlJobTrigger {
    private static Logger logger = LoggerFactory.getLogger(XxlJobTrigger.class);

    /**
     * trigger job
     *
     * @param jobId
     */
    public static void trigger(int jobId) {

        // load job
        XxlJobInfo jobInfo = XxlJobDynamicScheduler.xxlJobInfoDao.loadById(jobId);

        // log part-1
        XxlJobLog jobLog = new XxlJobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        XxlJobDynamicScheduler.xxlJobLogDao.save(jobLog);
        logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // log part-2 param
        //jobLog.setExecutorAddress(executorAddress);
        jobLog.setGlueType(jobInfo.getGlueType());
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setTriggerTime(new Date());

        // trigger request
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());

        // do trigger
        ReturnT<String> triggerResult = doTrigger(triggerParam, jobInfo, jobLog);

        // fail retry
        if (triggerResult.getCode()==ReturnT.FAIL_CODE &&
                ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == ExecutorFailStrategyEnum.FAIL_RETRY) {
            ReturnT<String> retryTriggerResult = doTrigger(triggerParam, jobInfo, jobLog);

            triggerResult.setCode(retryTriggerResult.getCode());
            triggerResult.setMsg(triggerResult.getMsg() + "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>失败重试<<<<<<<<<<< </span><br><br>" +retryTriggerResult.getMsg());
        }

        // log part-2
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerResult.getMsg());
        XxlJobDynamicScheduler.xxlJobLogDao.updateTriggerInfo(jobLog);

        // monitor triger
        JobFailMonitorHelper.monitor(jobLog.getId());
        logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }

    private static ReturnT<String> doTrigger(TriggerParam triggerParam, XxlJobInfo jobInfo, XxlJobLog jobLog){
        StringBuffer triggerSb = new StringBuffer();

        // exerutor address list
        ArrayList<String> addressList = null;
        XxlJobGroup group = XxlJobDynamicScheduler.xxlJobGroupDao.load(jobInfo.getJobGroup());
        if (group.getAddressType() == 0) {
            triggerSb.append("注册方式：自动注册");
            addressList = (ArrayList<String>) JobRegistryMonitorHelper.discover(RegistryConfig.RegistType.EXECUTOR.name(), group.getAppName());
        } else {
            triggerSb.append("注册方式：手动录入");
            if (StringUtils.isNotBlank(group.getAddressList())) {
                addressList = new ArrayList<String>(Arrays.asList(group.getAddressList().split(",")));
            }
        }
        triggerSb.append("<br>阻塞处理策略：").append(ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION).getTitle());
        triggerSb.append("<br>失败处理策略：").append(ExecutorFailStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorFailStrategyEnum.FAIL_ALARM).getTitle());
        triggerSb.append("<br>地址列表：").append(addressList!=null?addressList.toString():"");
        if (CollectionUtils.isEmpty(addressList)) {
            triggerSb.append("<br>----------------------<br>").append("调度失败：").append("执行器地址为空");
            return new ReturnT<String>(ReturnT.FAIL_CODE, triggerSb.toString());
        }

        // executor route strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        if (executorRouteStrategyEnum == null) {
            triggerSb.append("<br>----------------------<br>").append("调度失败：").append("执行器路由策略为空");
            return new ReturnT<String>(ReturnT.FAIL_CODE, triggerSb.toString());
        }
        triggerSb.append("<br>路由策略：").append(executorRouteStrategyEnum.name() + "-" + executorRouteStrategyEnum.getTitle());


        // route run / trigger remote executor
        ReturnT<String> routeRunResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList, jobLog);
        triggerSb.append("<br>----------------------<br>").append(routeRunResult.getMsg());
        return new ReturnT<String>(routeRunResult.getCode(), triggerSb.toString());

    }

}
