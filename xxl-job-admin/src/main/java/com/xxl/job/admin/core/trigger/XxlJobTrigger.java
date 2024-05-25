package com.xxl.job.admin.core.trigger;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.IpUtil;
import com.xxl.job.core.util.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

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
     * @param triggerType
     * @param failRetryCount        >=0: use this param
     *                              <0: use param from job info config
     * @param executorShardingParam
     * @param executorParam         null: use job param
     *                              not null: cover job param
     * @param addressList           null: use executor addressList
     *                              not null: cover
     */
    public static void trigger(int jobId,
                               TriggerTypeEnum triggerType,
                               int failRetryCount,
                               String executorShardingParam,
                               String executorParam,
                               String addressList) {

        // load data
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(jobId);
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }
        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(jobInfo.getJobGroup());

        // cover addressList
        if (addressList != null && addressList.trim().length() > 0) {
            group.setAddressType(1);
            group.setAddressList(addressList.trim());
        }

        // 初始化TriggerParam
        TriggerParam triggerParam = XxlJobTrigger.initTriggerParam(jobInfo);

        // 只对BEAN模式加了分片处理 其余没有
        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(jobInfo.getGlueType());
        if (GlueTypeEnum.BEAN==glueTypeEnum) {
            // 取消分片策略 任何任务都可只要实现shardHandler 都可以分片。任务分配工作根据节点的策略决定（第一个，一致性hash等）
            XxlJobTrigger.processShardTrigger(jobInfo, group, triggerParam, finalFailRetryCount, triggerType, addressList);
        } else {
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType, triggerParam);
        }


    }

    /**
     * java 分片处理 全部完成后才会触发后置节点；
     *  任务分片 交给执行器处理
     * @param jobInfo
     * @param group
     * @param triggerParam
     * @param finalFailRetryCount
     * @param triggerType
     * @param addressList
     */
    private static void processShardTrigger(XxlJobInfo jobInfo, XxlJobGroup group, TriggerParam triggerParam, int finalFailRetryCount, TriggerTypeEnum triggerType, String addressList) {
        List<String> executeList = new ArrayList<>();
        String msg = null;

        // 分片 依据调度策略进行分片
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);

        // 获取分片地址,地址为空时 在runShardExecutor中抛出异常
        String address = XxlJobTrigger.initAddress(group, executorRouteStrategyEnum, triggerParam);
        // 分片 开始调度时间
        Date triggerDate = new Date();
        try {
            ReturnT<List<String>> shardReturn = XxlJobTrigger.runShardExecutor(triggerParam, address);
            msg = shardReturn.getMsg();
            if (shardReturn.getCode() != ReturnT.SUCCESS_CODE) {
                logger.debug(">>>>>>>>>>>> Shard error,jobId={},{}", jobInfo.getId(), msg);
                XxlJobTrigger.updateXxlJobShardLogInfo(msg, jobInfo, addressList, finalFailRetryCount, triggerDate, ReturnT.FAIL_CODE);
                return;
            }
            if (shardReturn.getContent() != null) {
                executeList = shardReturn.getContent();
            }
        } catch (Exception e) {
            // shard 处理异常返回调度异常信息
            logger.error(">>>>>>>>>>>> shard error,jobId={},{}", jobInfo.getId(), e.getMessage());
            XxlJobTrigger.updateXxlJobShardLogInfo(e.getMessage(), jobInfo, addressList, finalFailRetryCount, triggerDate, ReturnT.FAIL_CODE);
            return;
        }
        // 没有异常,调度成功，没有要处理的数据
        if (executeList == null || executeList.isEmpty()) {
            // shard 返回结果为空,没有要处理的文件信息，执行结束,没有数据默认执行成功。
            XxlJobTrigger.updateXxlJobShardLogInfo(msg, jobInfo, addressList, finalFailRetryCount, triggerDate, ReturnT.FAIL_CODE);
            logger.info(">>>>>>>>>>>> shard success,没有要处理的数据 jobId invalid，jobId={}", jobInfo.getId());
            return;
        }

        for (String param : executeList) {
            // param 分发到不同的节点执行
            triggerParam.setExecutorParams(param);
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType, triggerParam);
        }
    }

    /**
     * @param group               job group, registry list may be empty
     * @param jobInfo
     * @param finalFailRetryCount
     * @param triggerType
     */
    private static void processTrigger(XxlJobGroup group, XxlJobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, TriggerParam triggerParam) {

        // param
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);  // block strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);    // route strategy

        // 1、save log-id
        XxlJobLog jobLog = new XxlJobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().save(jobLog);
        logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // 2、 trigger-param set log
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTime(jobLog.getTriggerTime().getTime());

        // 3、init address
        ReturnT<String> routeAddressResult = new ReturnT<>();
        String address = XxlJobTrigger.initAddress(group, executorRouteStrategyEnum, triggerParam);

        // 4、trigger remote executor
        ReturnT<String> triggerResult = null;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address);
        } else {
            triggerResult = new ReturnT<String>(ReturnT.FAIL_CODE, null);
            routeAddressResult = new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobconf_trigger_address_empty"));
        }

        // 5、collection trigger info
        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb.append(I18nUtil.getString("jobconf_trigger_type")).append("：").append(triggerType.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_admin_adress")).append("：").append(IpUtil.getIp());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regtype")).append("：")
                .append((group.getAddressType() == 0) ? I18nUtil.getString("jobgroup_field_addressType_0") : I18nUtil.getString("jobgroup_field_addressType_1"));
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regaddress")).append("：").append(group.getRegistryList());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorRouteStrategy")).append("：").append(executorRouteStrategyEnum.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorBlockStrategy")).append("：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_timeout")).append("：").append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorFailRetryCount")).append("：").append(finalFailRetryCount);

        triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_run") + "<<<<<<<<<<< </span><br>")
                .append((routeAddressResult != null && routeAddressResult.getMsg() != null) ? routeAddressResult.getMsg() + "<br><br>" : "").append(triggerResult.getMsg() != null ? triggerResult.getMsg() : "");

        // 6、save log trigger-info
        logger.debug(">>>>>>>>>> xxl-job trigger update db");
        XxlJobTrigger.updateXxlJobLogAfterExecute(jobLog,jobInfo,address,finalFailRetryCount,triggerResult,triggerMsgSb.toString());

        logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }

    /**
     * run executor
     *
     * @param triggerParam
     * @param address
     * @return
     */
    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<String>(ReturnT.FAIL_CODE, ThrowableUtil.toString(e));
        }

        StringBuilder runResultSB = XxlJobTrigger.setReturnMsg(address,runResult);
        runResult.setMsg(runResultSB.toString());
        return runResult;
    }


    /**
     * run shard executor
     * @param triggerParam
     * @param address
     * @return
     */
    private static ReturnT<List<String>> runShardExecutor(TriggerParam triggerParam,String address) throws Exception{
        ReturnT<List<String>> runShardResult;
        if (address == null) {
            logger.error(">>>>>>>>>>> xxl-job shard trigger error, please check if the executor is exist.");
            throw new XxlJobException("执行器节点 或 算法绑定执行器节点 未正常运行");
        }
        ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
        if (executorBiz == null) {
            //根据地址获取执行节点出错
            logger.error(">>>>>>>>>>> xxl-job shard trigger error, please check if the executor [{}] is running.",address);
            throw new XxlJobException("执行器节点不可用,请检查节点是否正常运行,<br> address:"+address);
        }
        runShardResult = executorBiz.runShard(triggerParam);
        if (runShardResult == null){
            logger.error(">>>>>>>>>>> xxl-job shard trigger error, please check if the shard is return. address:[{}]", address);
            throw new XxlJobException(triggerParam.getExecutorHandler()+"shard 没有设置返回结果");
        }
        StringBuilder runResultSb = XxlJobTrigger.setReturnMsg(address,runShardResult);
        runShardResult.setMsg(runResultSb.toString());
        return runShardResult;
    }

    /**
     * 初始化 triggerParam
     *
     * @param jobInfo 任务参数
     * @return
     */
    private static TriggerParam initTriggerParam(XxlJobInfo jobInfo) {
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());

        return triggerParam;
    }

    /**
     * 初始化执行节点
     *
     * @param group
     * @param executorRouteStrategyEnum
     * @param triggerParam
     * @return
     */
    private static String initAddress(XxlJobGroup group, ExecutorRouteStrategyEnum executorRouteStrategyEnum, TriggerParam triggerParam) {
        String address = null;
        if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
            // 自动注册 和 手动注册 两种方式先检查节点是否可用,并过滤掉不可用节点
            List<String> registryList = filterRegistryList(group.getRegistryList(), group.getAppname());
            if (!registryList.isEmpty()) {
                // 根据执行策略 找到要执行的节点
                ReturnT<String> routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, registryList);
                if (routeAddressResult.getCode() == ReturnT.SUCCESS_CODE) {
                    address = routeAddressResult.getContent();
                }
            }
        }
        return address;
    }

    /**
     * 判断当前节点是否可用，过滤掉不可用节点
     *
     * @param registryList
     * @param appName
     */
    private static List<String> filterRegistryList(List<String> registryList, String appName) {
        // 查询数据库中当前执行器可用节点
        List<String> dbRegistryList =
                XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().findRegistryValueList(RegistryConfig.DEAD_TIMEOUT, new Date(), appName);
        // 判断 registryList 是否有可用节点,有返回可用节点,没有返回空集合
        return Optional.ofNullable(dbRegistryList).orElse(new ArrayList<>())
                .stream()
                .filter(registryList::contains)
                .collect(Collectors.toList());
    }

    /**
     * shard 没有要处理的数据，生成日志 并返回
     *
     * @param msg
     * @param triggerDate
     */
    private static void updateXxlJobShardLogInfo(String msg, XxlJobInfo jobInfo, String addressList, Integer finalFailRetryCount, Date triggerDate, int code) {
        if (msg == null) {
            msg = I18nUtil.getString("shard_fail_msg");
        }
        XxlJobLog xxlJobLog = XxlJobTrigger.saveXxlJobLogBeforeExecute(jobInfo, triggerDate);
        XxlJobTrigger.updateXxlJobLogAfterExecute(xxlJobLog, jobInfo, addressList, finalFailRetryCount, ReturnT.SUCCESS, I18nUtil.getString("joblog_status_suc"));
        // 生成执行日志 并入库
        xxlJobLog.setHandleTime(new Date());
        xxlJobLog.setHandleCode(code);
        xxlJobLog.setHandleMsg(msg);
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateHandleInfo(xxlJobLog);
    }

    /**
     * handler 调度之前执行日志
     *
     * @param jobInfo
     */
    private static XxlJobLog saveXxlJobLogBeforeExecute(XxlJobInfo jobInfo, Date date) {
        XxlJobLog jobLog = new XxlJobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(date);
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().save(jobLog);
        return jobLog;
    }

    /**
     * 任务调度结束后，保存相关日志信息
     * @param jobLog
     * @param jobInfo
     * @param triggerResult
     * @param triggerMsgSb
     */
    private static void updateXxlJobLogAfterExecute(XxlJobLog jobLog, XxlJobInfo jobInfo, String address, Integer finalFailRetryCount, ReturnT<String> triggerResult, String triggerMsgSb) {
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb);
        logger.info("update trigger info,code:{},msg{}",triggerResult.getCode(),triggerMsgSb);
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(jobLog);
    }

    /**
     * 添加 调度返回信息
     * @param address
     * @param runResult
     * @return
     */
    private static StringBuilder setReturnMsg(String address, ReturnT<?> runResult) {
        StringBuilder runResultSB = new StringBuilder(I18nUtil.getString("jobconf_trigger_run") + "：");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMsg());

        return runResultSB;
    }
}
