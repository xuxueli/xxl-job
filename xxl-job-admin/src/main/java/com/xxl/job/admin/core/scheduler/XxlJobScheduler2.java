package com.xxl.job.admin.core.scheduler;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.thread.*;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.client.ExecutorBizClient;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xuxueli 2018-10-28 00:18:17
 */

public class XxlJobScheduler2 {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobScheduler2.class);

    public void init() throws Exception {
        // 国际化
        initI18n();

        // 开启JobTrigger 该helper定义了快慢两个线程池用来执行定时任务
        JobTriggerPoolHelper.toStart();

        // 开启执行器注册相关的任务 其中包含注册或移除执行器的线程池以及心跳监控线程
        JobRegistryHelper.getInstance().start();

        // 另起线程对失败任务做处理(jobLog表) 尝试进行重试(依赖JobTriggerPoolHelper)以及预警
        JobFailMonitorHelper.getInstance().start();

        // 对任务执行完成后的处理 即客户端调用callback 会借助该类中的callbackThreadPool进行处理
        // 同时另有一个monitor线程对处于运行中过长时间的任务进行处理
        JobCompleteHelper.getInstance().start();

        // 另起线程收集日志报告 主要用于报告展示 不是重点
        JobLogReportHelper.getInstance().start();

        // 开启调度 开启一个线程不断拉取待执行的任务 放入时间轮等待执行 时间轮线程扫描时间轮触发执行,最终依赖JobTriggerPoolHelper中的快慢线程池
        JobScheduleHelper.getInstance().start();

        logger.info(">>>>>>>>> init xxl-job admin success.");
    }


    public void destroy() throws Exception {

        // stop-schedule
        JobScheduleHelper.getInstance().toStop();

        // admin log report stop
        JobLogReportHelper.getInstance().toStop();

        // admin lose-monitor stop
        JobCompleteHelper.getInstance().toStop();

        // admin fail-monitor stop
        JobFailMonitorHelper.getInstance().toStop();

        // admin registry stop
        JobRegistryHelper.getInstance().toStop();

        // admin trigger pool stop
        JobTriggerPoolHelper.toStop();

    }

    // ---------------------- I18n ----------------------

    private void initI18n() {
        for (ExecutorBlockStrategyEnum item : ExecutorBlockStrategyEnum.values()) {
            item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
        }
    }

    // ---------------------- executor-client ----------------------
    // 为每一个远程地址address 创建一个ExecutorBizClient 存入本地缓存
    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<>();

    public static ExecutorBiz getExecutorBiz(String address) throws Exception {
        if (address == null || address.trim().length() == 0) {
            return null;
        }

        // 加载缓存 没有则new ExecutorBizClient
        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        executorBiz = new ExecutorBizClient(address, XxlJobAdminConfig.getAdminConfig().getAccessToken());

        executorBizRepository.put(address, executorBiz);
        return executorBiz;
    }
}
