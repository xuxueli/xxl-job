package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * job monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobFailMonitorHelper2 {
    private static Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper2.class);

    private static JobFailMonitorHelper2 instance = new JobFailMonitorHelper2();

    public static JobFailMonitorHelper2 getInstance() {
        return instance;
    }

    // ---------------------- monitor ----------------------

    private Thread monitorThread;
    private volatile boolean toStop = false;

    public void start() {
        monitorThread = new Thread(() -> {
            while (!toStop) {
                try {
                    // 查询所有执行失败的任务logId
                    List<Long> failLogIds = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().findFailJobLogIds(1000);
                    if (failLogIds != null && !failLogIds.isEmpty()) {
                        for (long failLogId : failLogIds) {

                            // 带条件的更新 更新失败则跳过说明有其他服务更新了 乐观锁
                            int lockRet = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateAlarmStatus(failLogId, 0, -1);
                            if (lockRet < 1) {
                                continue;
                            }
                            XxlJobLog log = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().load(failLogId);
                            XxlJobInfo info = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(log.getJobId());

                            // 1、对于失败的任务 如果失败重试次数大于0 则立即触发一次 下一次记录的failRetryCount为本次减一 直至为0
                            if (log.getExecutorFailRetryCount() > 0) {
                                JobTriggerPoolHelper.trigger(log.getJobId(), TriggerTypeEnum.RETRY, (log.getExecutorFailRetryCount() - 1), log.getExecutorShardingParam(), log.getExecutorParam(), null);
                                String retryMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_type_retry") + "<<<<<<<<<<< </span><br>";
                                log.setTriggerMsg(log.getTriggerMsg() + retryMsg);
                                XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(log);
                            }

                            // 2、任务失败预警 通过JobAlarmer
                            int newAlarmStatus;        // 告警状态：0-默认、-1=锁定状态、1-无需告警、2-告警成功、3-告警失败
                            if (info != null) {
                                boolean alarmResult = XxlJobAdminConfig.getAdminConfig().getJobAlarmer().alarm(info, log);
                                newAlarmStatus = alarmResult ? 2 : 3;
                            } else {
                                newAlarmStatus = 1;
                            }
                            // 更新锁住的jobLog
                            XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateAlarmStatus(failLogId, -1, newAlarmStatus);
                        }
                    }

                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }

            logger.info(">>>>>>>>>>> xxl-job, job fail monitor thread stop");

        });
        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin JobFailMonitorHelper");
        monitorThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
