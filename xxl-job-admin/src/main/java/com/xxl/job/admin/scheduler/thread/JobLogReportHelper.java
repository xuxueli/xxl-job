package com.xxl.job.admin.scheduler.thread;

import com.xxl.job.admin.model.XxlJobLogReport;
import com.xxl.job.admin.scheduler.conf.XxlJobAdminConfig;
import com.xxl.job.admin.platform.data.LogReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * job log report helper
 *
 * @author xuxueli 2019-11-22
 */
public class JobLogReportHelper {
    private static Logger logger = LoggerFactory.getLogger(JobLogReportHelper.class);

    private static JobLogReportHelper instance = new JobLogReportHelper();

    public static JobLogReportHelper getInstance() {
        return instance;
    }


    private Thread logrThread;
    private volatile boolean toStop = false;

    public void start() {
        logrThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // last clean log time
                long lastCleanLogTime = 0;


                while (!toStop) {

                    // 1、log-report refresh: refresh log report in 3 days
                    try {

                        for (int i = 0; i < 3; i++) {

                            // today
                            LocalDateTime itemDay = LocalDateTime.now().minusDays(i)
                                    .withHour(0)
                                    .withMinute(0)
                                    .withSecond(0)
                                    .withNano(0);

                            Date todayFrom = Date.from(
                                    itemDay.atZone(ZoneId.systemDefault())
                                            .toInstant()
                            );

                            itemDay = itemDay.plusDays(1)
                                    .minusSeconds(1);

                            Date todayTo = Date.from(
                                    itemDay.atZone(ZoneId.systemDefault())
                                            .toInstant()
                            );

                            // refresh log-report every minute
                            XxlJobLogReport xxlJobLogReport = new XxlJobLogReport();
                            xxlJobLogReport.setTriggerDay(todayFrom);
                            xxlJobLogReport.setRunningCount(0);
                            xxlJobLogReport.setSucCount(0);
                            xxlJobLogReport.setFailCount(0);

                            LogReportDto triggerCountMap = XxlJobAdminConfig.getAdminConfig().getXxlJobLogMapper().findLogReport(todayFrom, todayTo);


                            if (triggerCountMap!=null) {
                                int triggerDayCount = triggerCountMap.getTriggerDayCount();
                                int triggerDayCountRunning = triggerCountMap.getTriggerDayCountRunning();
                                int triggerDayCountSuc = triggerCountMap.getTriggerDayCountSuc();
                                int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

                                xxlJobLogReport.setRunningCount(triggerDayCountRunning);
                                xxlJobLogReport.setSucCount(triggerDayCountSuc);
                                xxlJobLogReport.setFailCount(triggerDayCountFail);
                            }

                            // do refresh
                            int ret = XxlJobAdminConfig.getAdminConfig().getXxlJobLogReportMapper().update(xxlJobLogReport);
                            if (ret < 1) {
                                XxlJobAdminConfig.getAdminConfig().getXxlJobLogReportMapper().save(xxlJobLogReport);
                            }
                        }

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job log report thread error:{}", e);
                        }
                    }

                    // 2、log-clean: switch open & once each day
                    if (XxlJobAdminConfig.getAdminConfig().getLogretentiondays() > 0
                            && System.currentTimeMillis() - lastCleanLogTime > 24 * 60 * 60 * 1000) {

                        // expire-time
                        LocalDateTime expiredDay = LocalDateTime.now()
                                .minusDays(1 * XxlJobAdminConfig.getAdminConfig().getLogretentiondays())
                                .withHour(0)
                                .withMinute(0)
                                .withSecond(0)
                                .withNano(0);

                        Date clearBeforeTime = Date.from(
                                expiredDay.atZone(ZoneId.systemDefault())
                                        .toInstant()
                        );

                        // clean expired log
                        List<Long> logIds = null;
                        do {
                            logIds = XxlJobAdminConfig.getAdminConfig().getXxlJobLogMapper().findClearLogIds(0, 0, clearBeforeTime, 0, 1000);
                            if (logIds != null && !logIds.isEmpty()) {
                                XxlJobAdminConfig.getAdminConfig().getXxlJobLogMapper().clearLog(logIds);
                            }
                        } while (logIds != null && !logIds.isEmpty());

                        // update clean time
                        lastCleanLogTime = System.currentTimeMillis();
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

    public void toStop() {
        toStop = true;
        // interrupt and wait
        logrThread.interrupt();
        try {
            logrThread.join();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

}
