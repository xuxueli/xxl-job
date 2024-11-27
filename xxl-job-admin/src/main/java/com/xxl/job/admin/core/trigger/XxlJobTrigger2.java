package com.xxl.job.admin.core.trigger;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
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
import com.xxl.job.core.util.IpUtil;
import com.xxl.job.core.util.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * xxl-job trigger
 * Created by xuxueli on 17/7/13.
 */
public class XxlJobTrigger2 {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobTrigger2.class);

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

        // 加载job信息
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(jobId);
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }
        // failRetryCount>=0才取其值是失败任务的关键 因为重试那里的判断是>0才进行重试 即从重试传过来的不会小于0所以也不会重复取任务的重试次数 默认的触发传值都为-1
        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(jobInfo.getJobGroup());

        // 这种情况是手动指定了执行地址
        if (addressList != null && addressList.trim().length() > 0) {
            group.setAddressType(1);
            group.setAddressList(addressList.trim());
        }

        int[] shardingParam = null;
        // 只有在重试的时候 这里才会有值 因为重试支持重试单个分片的任务 需要记录该任务的分片参数
        if (executorShardingParam != null) {
            String[] shardingArr = executorShardingParam.split("/");
            if (shardingArr.length == 2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                shardingParam = new int[2];
                shardingParam[0] = Integer.parseInt(shardingArr[0]);
                shardingParam[1] = Integer.parseInt(shardingArr[1]);
            }
        }
        // 只有分片广播是循环调用所有的执行地址  其他路由策略都是选择某一个执行地址
        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null)
                && group.getRegistryList() != null
                && !group.getRegistryList().isEmpty()
                && shardingParam == null) {
            for (int i = 0; i < group.getRegistryList().size(); i++) {
                // 循环调用执行地址
                // XxlJob这里只是传基础参数 具体分片任务的实现还要依赖客户端自己的任务设计
                // 客户端分片参数的获取可以使用XxlJobHelper.getShardIndex(),最终借用XxlJobContext这一ThreadLocal 该线程变量通过JobThread解析triggerParam存入
                processTrigger(group, jobInfo, finalFailRetryCount, triggerType, i, group.getRegistryList().size());
            }
        } else {
            // 默认传分片参数 1个分片
            if (shardingParam == null) {
                shardingParam = new int[]{0, 1};
            }
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param group               job group, registry list may be empty
     * @param jobInfo
     * @param finalFailRetryCount
     * @param triggerType
     * @param index               sharding index
     * @param total               sharding index
     */
    private static void processTrigger(XxlJobGroup group, XxlJobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) {
        // param
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) ? String.valueOf(index).concat("/").concat(String.valueOf(total)) : null;

        // 1、save log-id
        XxlJobLog jobLog = new XxlJobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().save(jobLog);
        logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTime(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);

        // 3、获取地址列表
        String address = null;
        ReturnT<String> routeAddressResult = null;
        if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
                // 对于分片广播 是在外部进行循环 传入index 所以这里直接通过下标获取address
                if (index < group.getRegistryList().size()) {
                    address = group.getRegistryList().get(index);
                } else {
                    address = group.getRegistryList().get(0);
                }
            } else {
                // 通过枚举维护了路由策略-->具体的策略类的映射
                // ExecutorRouter的子类实现具体的route方法
                routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
                if (routeAddressResult.getCode() == ReturnT.SUCCESS_CODE) {
                    address = routeAddressResult.getContent();
                }
            }
        } else {
            routeAddressResult = new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("jobconf_trigger_address_empty"));
        }

        // 4、通过具体的address 进行rpc调用
        ReturnT<String> triggerResult;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address);
        } else {
            triggerResult = new ReturnT<>(ReturnT.FAIL_CODE, null);
        }
        // 5、打印触发日志 不是重点 省略掉了
        StringBuilder triggerMsgSb = new StringBuilder();

        // 6、更新执行实例信息
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(jobLog);

        logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }

    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult;
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
            // ExecutorBizClient单纯的http调用 远程服务通过netty响应
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<>(ReturnT.FAIL_CODE, ThrowableUtil.toString(e));
        }
        // 省略拼接msg
        StringBuffer runResultSB = new StringBuffer(I18nUtil.getString("jobconf_trigger_run") + "：");
        runResult.setMsg(runResultSB.toString());
        return runResult;
    }
}
