package com.xxl.job.admin.core.thread;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.dao.XxlJobLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * job log report helper
 *
 * @author xuxueli 2019-11-22
 */
public class JobLogReportHelper {

    private static final Logger logger = LoggerFactory.getLogger(JobLogReportHelper.class);

    private static final JobLogReportHelper instance = new JobLogReportHelper();

    public static JobLogReportHelper getInstance() {
        return instance;
    }

    private Thread logrThread;
    private volatile boolean toStop = false;

    public static int castAsInt(Object val, int defaultVal) {
        if (val instanceof Number) {
            return ((Number) val).intValue();
        } else if (val == null) {
            return defaultVal;
        }
        return Integer.parseInt(val.toString());
    }

    public void start() {
        logrThread = new Thread(() -> {

            // last clean log time
            long lastCleanLogTime = 0;

            while (!toStop) {

                // 1、log-report refresh: refresh log report in 3 days
                final XxlJobAdminConfig adminConfig = XxlJobAdminConfig.getAdminConfig();
                final XxlJobLogDao xxlJobLogDao = adminConfig.getXxlJobLogDao();
                try {

                    for (int i = 0; i < 3; i++) {

                        // today
                        Calendar itemDay = Calendar.getInstance();
                        itemDay.add(Calendar.DAY_OF_MONTH, -i);
                        itemDay.set(Calendar.HOUR_OF_DAY, 0);
                        itemDay.set(Calendar.MINUTE, 0);
                        itemDay.set(Calendar.SECOND, 0);
                        itemDay.set(Calendar.MILLISECOND, 0);

                        Date todayFrom = itemDay.getTime();

                        itemDay.set(Calendar.HOUR_OF_DAY, 23);
                        itemDay.set(Calendar.MINUTE, 59);
                        itemDay.set(Calendar.SECOND, 59);
                        itemDay.set(Calendar.MILLISECOND, 999);

                        Date todayTo = itemDay.getTime();

                        // refresh log-report every minute
                        XxlJobLogReport xxlJobLogReport = new XxlJobLogReport();
                        xxlJobLogReport.setTriggerDay(todayFrom);
                        xxlJobLogReport.setRunningCount(0);
                        xxlJobLogReport.setSucCount(0);
                        xxlJobLogReport.setFailCount(0);

                        Map<String, Object> triggerCountMap = xxlJobLogDao.findLogReport(todayFrom, todayTo);
                        if (triggerCountMap != null && !triggerCountMap.isEmpty()) {
                            int triggerDayCount = castAsInt(triggerCountMap.get("triggerDayCount"), 0);
                            int triggerDayCountRunning = castAsInt(triggerCountMap.get("triggerDayCountRunning"), 0);
                            int triggerDayCountSuc = castAsInt(triggerCountMap.get("triggerDayCountSuc"), 0);
                            int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

                            xxlJobLogReport.setRunningCount(triggerDayCountRunning);
                            xxlJobLogReport.setSucCount(triggerDayCountSuc);
                            xxlJobLogReport.setFailCount(triggerDayCountFail);
                        }

                        // do refresh
                        int ret = adminConfig.getXxlJobLogReportDao().update(xxlJobLogReport);
                        if (ret < 1) {
                            adminConfig.getXxlJobLogReportDao().save(xxlJobLogReport);
                        }
                    }

                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(">>>>>>>>>>> xxl-job, job log report thread error", e);
                    }
                }

                // 2、log-clean: switch open & once each day
                if (adminConfig.getLogretentiondays() > 0
                        && System.currentTimeMillis() - lastCleanLogTime > 24 * 60 * 60 * 1000) {

                    // expire-time
                    Calendar expiredDay = Calendar.getInstance();
                    expiredDay.add(Calendar.DAY_OF_MONTH, -1 * adminConfig.getLogretentiondays());
                    expiredDay.set(Calendar.HOUR_OF_DAY, 0);
                    expiredDay.set(Calendar.MINUTE, 0);
                    expiredDay.set(Calendar.SECOND, 0);
                    expiredDay.set(Calendar.MILLISECOND, 0);
                    Date clearBeforeTime = expiredDay.getTime();

                    // clean expired log
                    clearLogs(xxlJobLogDao, 0, 0, clearBeforeTime, 0);

                    // update clean time
                    lastCleanLogTime = System.currentTimeMillis();
                }

                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }

            logger.info(">>>>>>>>>>> xxl-job, job log report thread stop");

        });
        logrThread.setDaemon(true);
        logrThread.setName("xxl-job, admin JobLogReportHelper");
        logrThread.start();
    }

    public static void clearLogs(final XxlJobLogDao xxlJobLogDao, int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum) {
        final int pageSize = 1000;
        int size;
        do {
            List<Long> logIds = xxlJobLogDao.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, pageSize);
            size = logIds.size();
            if (size > 0) {
                xxlJobLogDao.clearLog(logIds);
            }
        } while (size == pageSize);
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        logrThread.interrupt();
        try {
            logrThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
