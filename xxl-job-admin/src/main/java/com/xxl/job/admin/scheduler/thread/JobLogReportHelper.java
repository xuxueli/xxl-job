package com.xxl.job.admin.scheduler.thread;

import com.xxl.job.admin.scheduler.conf.XxlJobAdminConfig;
import com.xxl.job.admin.model.XxlJobLogReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * job log report helper
 *
 * @author xuxueli 2019-11-22
 */
public class JobLogReportHelper {
    private static final Logger logger = LoggerFactory.getLogger(JobLogReportHelper.class);

    private static final JobLogReportHelper INSTANCE = new JobLogReportHelper();
    public static JobLogReportHelper getInstance(){
        return INSTANCE;
    }

    /**
     * 刷新最近 N 天的日志
     */
    private static final int REPORT_DAYS = 3;

    private Thread logrThread;
    private volatile boolean toStop = false;

    public void start(){
        
        logrThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // last clean log time
                long lastCleanLogTime = 0;


                while (!toStop) {

                    // 1、log-report refresh: refresh log report in 3 days
                    try {
                        refreshLogReport();
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job log report thread error:{}", e, e);
                        }
                    }

                    try {
                        // 2、log-clean: switch open & once each day
                        final int retentionDays = XxlJobAdminConfig.getAdminConfig().getLogretentiondays();
                        if (retentionDays > 0
                                && System.currentTimeMillis() - lastCleanLogTime > 24 * 60 * 60 * 1000) {
                            cleanExpiredLogs(retentionDays);
                            // update clean time
                            lastCleanLogTime = System.currentTimeMillis();
                        }
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job log report thread error:{}", e, e);
                        }
                    }

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                }

                logger.info(">>>>>>>>>>> xxl-job, job log report thread stop");

            }
        });
        logrThread.setDaemon(true);
        logrThread.setName("xxl-job, admin JobLogReportHelper");
        logrThread.start();
    }

    public void toStop(){
        toStop = true;
        // interrupt and wait
        logrThread.interrupt();
        try {
            logrThread.join();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 每天清理一次过期日志
     * @param days 过期天数
     */
    private static void cleanExpiredLogs(int days) {
        // expire-time
        Date clearBeforeTime = getDayStart(-1 * days);

        // clean expired log
        List<Long> logIds;
        do {
            logIds = XxlJobAdminConfig.getAdminConfig().getXxlJobLogMapper().findClearLogIds(0, 0, clearBeforeTime, 0, 1000);
            if (logIds != null && !logIds.isEmpty()) {
                XxlJobAdminConfig.getAdminConfig().getXxlJobLogMapper().clearLog(logIds);
            }
        } while (logIds != null && !logIds.isEmpty());
    }

    /**
     * 刷新最近 REPORT_DAYS 天的日志统计
     */
    private static void refreshLogReport() {
        for (int i = 0; i < REPORT_DAYS; i++) {
            Date start = getDayStart(-i);
            Date end = getDayEnd(-i);

            XxlJobLogReport report = new XxlJobLogReport();
            report.setTriggerDay(start);
            report.setRunningCount(0);
            report.setSucCount(0);
            report.setFailCount(0);

            Map<String, Object> triggerCountMap =
                    XxlJobAdminConfig.getAdminConfig().getXxlJobLogMapper().findLogReport(start, end);

            if (triggerCountMap != null && !triggerCountMap.isEmpty()) {
                int triggerDayCount = triggerCountMap.containsKey("triggerDayCount") ? Integer.parseInt(String.valueOf(triggerCountMap.get("triggerDayCount"))) : 0;
                int triggerDayCountRunning = triggerCountMap.containsKey("triggerDayCountRunning") ? Integer.parseInt(String.valueOf(triggerCountMap.get("triggerDayCountRunning"))) : 0;
                int triggerDayCountSuc = triggerCountMap.containsKey("triggerDayCountSuc") ? Integer.parseInt(String.valueOf(triggerCountMap.get("triggerDayCountSuc"))) : 0;
                int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;
                
                report.setRunningCount(triggerDayCountRunning);
                report.setSucCount(triggerDayCountSuc);
                report.setFailCount(triggerDayCountFail);
            }

            int ret = XxlJobAdminConfig.getAdminConfig().getXxlJobLogReportMapper().update(report);
            if (ret < 1) {
                XxlJobAdminConfig.getAdminConfig().getXxlJobLogReportMapper().save(report);
            }
        }
    }

    private static Date getDayStart(int offsetDays) {
        LocalDateTime startOfDay = LocalDate.now()
                .plusDays(offsetDays)
                .atStartOfDay();
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static Date getDayEnd(int offsetDays) {
        LocalDateTime endOfDay = LocalDate.now()
                .plusDays(offsetDays)
                // 23:59:59.999999999
                .atTime(LocalTime.MAX); 
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

}
