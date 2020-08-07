package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author xuxueli 2019-05-21
 */
public class JobScheduleHelper {
    private static Logger logger = LoggerFactory.getLogger(JobScheduleHelper.class);

    private static JobScheduleHelper instance = new JobScheduleHelper();

    public static JobScheduleHelper getInstance() {
        return instance;
    }

    // pre read
    public static final long PRE_READ_MS = 5000;

    private ScheduledThreadPoolExecutor scheduleExecutor;
    private ExecutorService fetchExecutor;
    private ExecutorService triggerExecutor;

    public void start() {
        scheduleExecutor = new ScheduledThreadPoolExecutor(1);
        triggerExecutor = Executors.newFixedThreadPool(10);
        fetchExecutor = Executors.newSingleThreadExecutor();

        // schedule thread
        scheduleExecutor.schedule(new FetchTask(fetchExecutor, scheduleExecutor), PRE_READ_MS, TimeUnit.MICROSECONDS);
    }

    private class FetchTask implements Runnable {

        private ExecutorService executorService;

        private ScheduledThreadPoolExecutor scheduleExecutor;

        public FetchTask(ExecutorService executorService, ScheduledThreadPoolExecutor scheduleExecutor) {
            this.executorService = executorService;
            this.scheduleExecutor = scheduleExecutor;
        }

        @Override
        public void run() {
            executorService.execute(() -> {
                scheduleExecutor.schedule(this, PRE_READ_MS, TimeUnit.MICROSECONDS);
                logger.info(">>>>>>>>> init xxl-job admin scheduler success.");

                // pre-read count: treadpool-size * trigger-qps (each trigger cost 50ms, qps = 1000/50 = 20)
                int preReadCount = (XxlJobAdminConfig.getAdminConfig().getTriggerPoolFastMax() + XxlJobAdminConfig.getAdminConfig().getTriggerPoolSlowMax()) * 20;

                Connection conn = null;
                boolean connAutoCommit = false;
                PreparedStatement preparedStatement = null;

                try {
                    conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
                    connAutoCommit = conn.getAutoCommit();
                    conn.setAutoCommit(false);

                    preparedStatement = conn.prepareStatement("select * from xxl_job_lock where lock_name = 'schedule_lock' for update");
                    preparedStatement.execute();

                    // tx start

                    // 1、pre read
                    long nowTime = System.currentTimeMillis();
                    List<XxlJobInfo> scheduleList = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleJobQuery(nowTime + PRE_READ_MS, preReadCount);
                    if (scheduleList.isEmpty()) {
                        return;
                    }

                    // 2、push time-ring
                    for (XxlJobInfo jobInfo : scheduleList) {
                        // time-ring jump
                        if (nowTime > jobInfo.getTriggerNextTime() + PRE_READ_MS) {
                            // 2.1、trigger-expire > 5s：pass && make next-trigger-time
                            logger.warn(">>>>>>>>>>> xxl-job, schedule misfire, jobId = " + jobInfo.getId());
                            // fresh next
                            refreshNextValidTime(jobInfo, new Date());

                        } else if (nowTime > jobInfo.getTriggerNextTime()) {
                            // 2.2、trigger-expire < 5s：direct-trigger && make next-trigger-time

                            // 1、trigger
                            JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.CRON, -1, null, null, null);
                            logger.debug(">>>>>>>>>>> xxl-job, schedule push trigger : jobId = " + jobInfo.getId());

                            // 2、fresh next
                            refreshNextValidTime(jobInfo, new Date());

                            // next-trigger-time in 5s, pre-read again
                            if (jobInfo.getTriggerStatus() == 1 && nowTime + PRE_READ_MS > jobInfo.getTriggerNextTime()) {
                                long ringTime = jobInfo.getTriggerNextTime() - System.currentTimeMillis();
                                scheduleExecutor.schedule(new RingTask(triggerExecutor, jobInfo.getId()), ringTime, TimeUnit.MICROSECONDS);
                                refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                            }
                        } else {
                            long ringTime = jobInfo.getTriggerNextTime() - System.currentTimeMillis();
                            scheduleExecutor.schedule(new RingTask(triggerExecutor, jobInfo.getId()), ringTime, TimeUnit.MICROSECONDS);
                            refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                        }
                    }

                    // 3、update trigger info
                    for (XxlJobInfo jobInfo : scheduleList) {
                        XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleUpdate(jobInfo);
                    }
                } catch (Exception e) {
                    logger.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread error:{}", e);
                } finally {
                    // commit
                    if (conn != null) {
                        try {
                            conn.commit();
                        } catch (SQLException e) {
                            logger.error(e.getMessage(), e);
                        }
                        try {
                            conn.setAutoCommit(connAutoCommit);
                        } catch (SQLException e) {
                            logger.error(e.getMessage(), e);
                        }
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    // close PreparedStatement
                    if (null != preparedStatement) {
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread stop");
            });
        }


        private class RingTask implements Runnable {

            private int jobId;

            private ExecutorService executor;

            RingTask(ExecutorService executor, int jobId) {
                this.executor = executor;
                this.jobId = jobId;
            }

            @Override
            public void run() {
                try {
                    executor.execute(() -> JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.CRON, -1, null, null, null));
                } catch (Exception e) {
                }
                logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread stop");
            }
        }

        private void refreshNextValidTime(XxlJobInfo jobInfo, Date fromTime) throws ParseException {
            Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(fromTime);
            if (nextValidTime != null) {
                jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
                jobInfo.setTriggerNextTime(nextValidTime.getTime());
            } else {
                jobInfo.setTriggerStatus(0);
                jobInfo.setTriggerLastTime(0);
                jobInfo.setTriggerNextTime(0);
            }
        }

        public void toStop() {
            scheduleExecutor.shutdown();
            fetchExecutor.shutdown();
            executorService.shutdown();
        }
    }
}

