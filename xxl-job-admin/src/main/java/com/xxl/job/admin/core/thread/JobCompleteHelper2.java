package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * job lose-monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobCompleteHelper2 {
    private static final Logger logger = LoggerFactory.getLogger(JobCompleteHelper2.class);

    private static final JobCompleteHelper2 instance = new JobCompleteHelper2();

    public static JobCompleteHelper2 getInstance() {
        return instance;
    }

    // ---------------------- monitor ----------------------

    private ThreadPoolExecutor callbackThreadPool = null;
    private Thread monitorThread;
    private volatile boolean toStop = false;

    public void start() {

        // 用于执行客户端回调的线程池
        callbackThreadPool = new ThreadPoolExecutor(
                2,
                20,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3000),
                r -> new Thread(r, "xxl-job, admin JobLosedMonitorHelper-callbackThreadPool-" + r.hashCode()),
                (r, executor) -> {
                    r.run();
                    logger.warn(">>>>>>>>>>> xxl-job, callback too fast, match threadpool rejected handler(run now).");
                });

        // 监控线程 将处于运行中状态10m且其执行器心跳超过规定时间的任务视为结果丢失(即未成功处理回调)
        monitorThread = new Thread(() -> {

            // wait for JobTriggerPoolHelper-init
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                if (!toStop) {
                    logger.error(e.getMessage(), e);
                }
            }

            while (!toStop) {
                try {
                    // 任务结果丢失处理：调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线，则将本地调度主动标记失败
                    Date losedTime = DateUtil.addMinutes(new Date(), -10);
                    List<Long> losedJobIds = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().findLostJobIds(losedTime);

                    if (losedJobIds != null && losedJobIds.size() > 0) {
                        for (Long logId : losedJobIds) {

                            XxlJobLog jobLog = new XxlJobLog();
                            jobLog.setId(logId);
                            jobLog.setHandleTime(new Date());
                            jobLog.setHandleCode(ReturnT.FAIL_CODE);
                            jobLog.setHandleMsg(I18nUtil.getString("joblog_lost_fail"));

                            XxlJobCompleter.updateHandleInfoAndFinish(jobLog);
                        }
                    }
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(60);
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }
            logger.info(">>>>>>>>>>> xxl-job, JobLosedMonitorHelper stop");
        });
        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin JobLosedMonitorHelper");
        monitorThread.start();
    }

    public void toStop() {
        toStop = true;

        // stop registryOrRemoveThreadPool
        callbackThreadPool.shutdownNow();

        // stop monitorThread (interrupt and wait)
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------

    /**
     * 该方法是客户端rpc调用的入口
     */
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        // 响应客户端的回调请求 将其放入线程池处理
        callbackThreadPool.execute(() -> {
            for (HandleCallbackParam handleCallbackParam : callbackParamList) {
                // 循环回调
                ReturnT<String> callbackResult = callback(handleCallbackParam);
                logger.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                        (callbackResult.getCode() == ReturnT.SUCCESS_CODE ? "success" : "fail"), handleCallbackParam, callbackResult);
            }
        });
        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // 检验记录是否存在
        XxlJobLog log = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "log item not found.");
        }
        // 避免重复回调 重复触发子任务 handleCode为int私有类型,默认为0
        if (log.getHandleCode() > 0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "log repeate callback.");
        }

        // 追加日志
        StringBuilder handleMsg = new StringBuilder();
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getHandleMsg() != null) {
            handleMsg.append(handleCallbackParam.getHandleMsg());
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getHandleCode());
        log.setHandleMsg(handleMsg.toString());
        // 这里是关键 完成任务 触发子任务调用之类的
        XxlJobCompleter.updateHandleInfoAndFinish(log);

        return ReturnT.SUCCESS;
    }
}
