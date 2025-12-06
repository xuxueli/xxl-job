package com.xxl.job.admin.scheduler.thread;

import com.xxl.job.admin.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.model.XxlJobLogReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
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


    private Thread logReportThread;
    private volatile boolean toStop = false;

    /**
     * start
     */
    public void start(){
        logReportThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // last clean log time
                long lastCleanLogTime = 0;


                while (!toStop) {

                    // 1、log-report refresh: refresh log report in 3 days
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

                            Map<String, Object> triggerCountMap = XxlJobAdminBootstrap.getInstance().getXxlJobLogMapper().findLogReport(todayFrom, todayTo);
                            if (triggerCountMap!=null && !triggerCountMap.isEmpty()) {
                                int triggerDayCount = triggerCountMap.containsKey("triggerDayCount")?Integer.parseInt(String.valueOf(triggerCountMap.get("triggerDayCount"))):0;
                                int triggerDayCountRunning = triggerCountMap.containsKey("triggerDayCountRunning")?Integer.parseInt(String.valueOf(triggerCountMap.get("triggerDayCountRunning"))):0;
                                int triggerDayCountSuc = triggerCountMap.containsKey("triggerDayCountSuc")?Integer.parseInt(String.valueOf(triggerCountMap.get("triggerDayCountSuc"))):0;
                                int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

                                xxlJobLogReport.setRunningCount(triggerDayCountRunning);
                                xxlJobLogReport.setSucCount(triggerDayCountSuc);
                                xxlJobLogReport.setFailCount(triggerDayCountFail);
                            }

                            // do refresh:
                            XxlJobAdminBootstrap.getInstance().getXxlJobLogReportMapper().saveOrUpdate(xxlJobLogReport);      // 0-fail; 1-save suc; 2-update suc;
                            /*if (ret < 1) {
                                XxlJobAdminBootstrap.getInstance().getXxlJobLogReportMapper().save(xxlJobLogReport);
                            }*/
                        }

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobLogReportHelper(log-report refresh) error:{}", e.getMessage(), e);
                        }
                    }

                    // 2、log-clean: switch open & once each day
                    try {
                        if (XxlJobAdminBootstrap.getInstance().getLogretentiondays()>0
                                && System.currentTimeMillis() - lastCleanLogTime > 24*60*60*1000) {

                            // expire-time
                            Calendar expiredDay = Calendar.getInstance();
                            expiredDay.add(Calendar.DAY_OF_MONTH, -1 * XxlJobAdminBootstrap.getInstance().getLogretentiondays());
                            expiredDay.set(Calendar.HOUR_OF_DAY, 0);
                            expiredDay.set(Calendar.MINUTE, 0);
                            expiredDay.set(Calendar.SECOND, 0);
                            expiredDay.set(Calendar.MILLISECOND, 0);
                            Date clearBeforeTime = expiredDay.getTime();

                            // clean expired log
                            List<Long> logIds = null;
                            do {
                                logIds = XxlJobAdminBootstrap.getInstance().getXxlJobLogMapper().findClearLogIds(0, 0, clearBeforeTime, 0, 1000);
                                if (logIds!=null && !logIds.isEmpty()) {
                                    XxlJobAdminBootstrap.getInstance().getXxlJobLogMapper().clearLog(logIds);
                                }
                            } while (logIds!=null && !logIds.isEmpty());

                            // update clean time
                            lastCleanLogTime = System.currentTimeMillis();
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobLogReportHelper(log-clean) error:{}", e.getMessage(), e);
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
        logReportThread.setDaemon(true);
        logReportThread.setName("xxl-job, admin JobLogReportHelper");
        logReportThread.start();
    }

    /**
     * stop
     */
    public void stop(){
        toStop = true;
        // interrupt and wait
        logReportThread.interrupt();
        try {
            logReportThread.join();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

}
