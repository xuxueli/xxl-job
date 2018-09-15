package com.xxl.job.admin.core.trigger;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.core.thread.JobFailMonitorHelper;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
     * @param failRetryCount
     * 			>=0: use this param
     * 			<0: use param from job info config
     *
     */
    public static void trigger(int jobId, int failRetryCount, TriggerTypeEnum triggerType) {

        // load data
        XxlJobInfo jobInfo = XxlJobDynamicScheduler.xxlJobInfoDao.loadById(jobId);              // job info
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        int finalFailRetryCount = jobInfo.getExecutorFailRetryCount();
        if (failRetryCount >= 0) {
            finalFailRetryCount = failRetryCount;
        }

        XxlJobGroup group = XxlJobDynamicScheduler.xxlJobGroupDao.load(jobInfo.getJobGroup());  // group info

        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);  // block strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);    // route strategy
        ArrayList<String> addressList = (ArrayList<String>) group.getRegistryList();

        // broadcast
        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum && CollectionUtils.isNotEmpty(addressList)) {
            for (int i = 0; i < addressList.size(); i++) {
                String address = addressList.get(i);

                // 1、save log-id
                XxlJobLog jobLog = new XxlJobLog();
                jobLog.setJobGroup(jobInfo.getJobGroup());
                jobLog.setJobId(jobInfo.getId());
                XxlJobDynamicScheduler.xxlJobLogDao.save(jobLog);
                logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

                // 2、prepare trigger-info
                //jobLog.setExecutorAddress(executorAddress);
                jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
                jobLog.setExecutorParam(jobInfo.getExecutorParam());
                jobLog.setExecutorFailRetryCount(finalFailRetryCount);
                jobLog.setTriggerTime(new Date());

                ReturnT<String> triggerResult = new ReturnT<String>(null);
                StringBuffer triggerMsgSb = new StringBuffer();
                triggerMsgSb.append(I18nUtil.getString("jobconf_trigger_type")).append("：").append(triggerType.getTitle());
                triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_admin_adress")).append("：").append(IpUtil.getIp());
                triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regtype")).append("：")
                        .append( (group.getAddressType() == 0)?I18nUtil.getString("jobgroup_field_addressType_0"):I18nUtil.getString("jobgroup_field_addressType_1") );
                triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regaddress")).append("：").append(group.getRegistryList());
                triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorRouteStrategy")).append("：").append(executorRouteStrategyEnum.getTitle()).append("("+i+"/"+addressList.size()+")"); // update01
                triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorBlockStrategy")).append("：").append(blockStrategy.getTitle());
                triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_timeout")).append("：").append(jobInfo.getExecutorTimeout());
                triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorFailRetryCount")).append("：").append(finalFailRetryCount);


                // 3.1、trigger-param
                TriggerParam triggerParam = new TriggerParam();
                triggerParam.setJobId(jobInfo.getId());
                triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
                triggerParam.setExecutorParams(jobInfo.getExecutorParam());
                triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
                triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
                triggerParam.setLogId(jobLog.getId());
                triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
                triggerParam.setGlueType(jobInfo.getGlueType());
                triggerParam.setGlueSource(jobInfo.getGlueSource());
                triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
                triggerParam.setBroadcastIndex(i);
                triggerParam.setBroadcastTotal(addressList.size()); // update02

                // 3.2、trigger-run (route run / trigger remote executor)
                triggerResult = runExecutor(triggerParam, address);     // update03
                triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_run") +"<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());

                // 4、save trigger-info
                jobLog.setExecutorAddress(triggerResult.getContent());
                jobLog.setTriggerCode(triggerResult.getCode());
                jobLog.setTriggerMsg(triggerMsgSb.toString());
                XxlJobDynamicScheduler.xxlJobLogDao.updateTriggerInfo(jobLog);

                // 5、monitor trigger
                JobFailMonitorHelper.monitor(jobLog.getId());
                logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());

            }

        } else {
            // 1、save log-id
            XxlJobLog jobLog = new XxlJobLog();
            jobLog.setJobGroup(jobInfo.getJobGroup());
            jobLog.setJobId(jobInfo.getId());
            XxlJobDynamicScheduler.xxlJobLogDao.save(jobLog);
            logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

            // 2、prepare trigger-info
            //jobLog.setExecutorAddress(executorAddress);
            jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
            jobLog.setExecutorParam(jobInfo.getExecutorParam());
            jobLog.setExecutorFailRetryCount(finalFailRetryCount);
            jobLog.setTriggerTime(new Date());

            ReturnT<String> triggerResult = new ReturnT<String>(null);
            StringBuffer triggerMsgSb = new StringBuffer();
            triggerMsgSb.append(I18nUtil.getString("jobconf_trigger_type")).append("：").append(triggerType.getTitle());
            triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_admin_adress")).append("：").append(IpUtil.getIp());
            triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regtype")).append("：")
                    .append( (group.getAddressType() == 0)?I18nUtil.getString("jobgroup_field_addressType_0"):I18nUtil.getString("jobgroup_field_addressType_1") );
            triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regaddress")).append("：").append(group.getRegistryList());
            triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorRouteStrategy")).append("：").append(executorRouteStrategyEnum.getTitle());
            triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorBlockStrategy")).append("：").append(blockStrategy.getTitle());
            triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_timeout")).append("：").append(jobInfo.getExecutorTimeout());
            triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorFailRetryCount")).append("：").append(finalFailRetryCount);

            // 3.0、trigger-valid
            if (CollectionUtils.isEmpty(addressList)) {
                triggerResult.setCode(ReturnT.FAIL_CODE);
                triggerMsgSb.append("<br>----------------------<br>").append(I18nUtil.getString("jobconf_trigger_address_empty"));
            } else {
                // 3.1、trigger-param
                TriggerParam triggerParam = new TriggerParam();
                triggerParam.setJobId(jobInfo.getId());
                triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
                triggerParam.setExecutorParams(jobInfo.getExecutorParam());
                triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
                triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
                triggerParam.setLogId(jobLog.getId());
                triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
                triggerParam.setGlueType(jobInfo.getGlueType());
                triggerParam.setGlueSource(jobInfo.getGlueSource());
                triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
                triggerParam.setBroadcastIndex(0);
                triggerParam.setBroadcastTotal(1);

                // 3.2、trigger-run (route run / trigger remote executor)
                triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
                triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_run") +"<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());


            }

            // 4、save trigger-info
            jobLog.setExecutorAddress(triggerResult.getContent());
            jobLog.setTriggerCode(triggerResult.getCode());
            jobLog.setTriggerMsg(triggerMsgSb.toString());
            XxlJobDynamicScheduler.xxlJobLogDao.updateTriggerInfo(jobLog);

            // 5、monitor trigger
            JobFailMonitorHelper.monitor(jobLog.getId());
            logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
        }

    }

    /**
     * run executor
     * @param triggerParam
     * @param address
     * @return  ReturnT.content: final address
     */
    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address){
        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = XxlJobDynamicScheduler.getExecutorBiz(address);
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<String>(ReturnT.FAIL_CODE, ""+e );
        }

        StringBuffer runResultSB = new StringBuffer(I18nUtil.getString("jobconf_trigger_run") + "：");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMsg());

        runResult.setMsg(runResultSB.toString());
        runResult.setContent(address);
        return runResult;
    }

}
