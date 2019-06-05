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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2019-05-21
 */
public class JobScheduleHelper {
    private static Logger logger = LoggerFactory.getLogger(JobScheduleHelper.class);

    private static JobScheduleHelper instance = new JobScheduleHelper();
    public static JobScheduleHelper getInstance(){
        return instance;
    }

    private Thread scheduleThread;
    private Thread ringThread;
    private volatile boolean toStop = false;
    private volatile static Map<Integer, List<Integer>> ringData = new ConcurrentHashMap<>();

    public void start(){

        // schedule thread
        scheduleThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis()%1000 );
                } catch (InterruptedException e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.info(">>>>>>>>> init xxl-job admin scheduler success.");

                while (!toStop) {

                    // 扫描任务
                    long start = System.currentTimeMillis();
                    Connection conn = null;
                    PreparedStatement preparedStatement = null;
                    try {
                        if (conn==null || conn.isClosed()) {
                            conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
                        }
                        conn.setAutoCommit(false);

                        preparedStatement = conn.prepareStatement(  "select * from xxl_job_lock where lock_name = 'schedule_lock' for update" );
                        preparedStatement.execute();

                        // tx start

                        // 1、预读10s内调度任务
                        long maxNextTime = System.currentTimeMillis() + 10000;
                        long nowTime = System.currentTimeMillis();
                        List<XxlJobInfo> scheduleList = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleJobQuery(maxNextTime);
                        if (scheduleList!=null && scheduleList.size()>0) {
                            // 2、推送时间轮
                            for (XxlJobInfo jobInfo: scheduleList) {

                                // 时间轮刻度计算
                                int ringSecond = -1;
                                if (jobInfo.getTriggerNextTime() < nowTime - 10000) {   // 过期超10s：本地忽略，当前时间开始计算下次触发时间
                                    ringSecond = -1;

                                    jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
                                    jobInfo.setTriggerNextTime(
                                            new CronExpression(jobInfo.getJobCron())
                                                    .getNextValidTimeAfter(new Date())
                                                    .getTime()
                                    );
                                } else if (jobInfo.getTriggerNextTime() < nowTime) {    // 过期10s内：立即触发一次，当前时间开始计算下次触发时间
                                    ringSecond = (int)((nowTime/1000)%60);

                                    jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
                                    jobInfo.setTriggerNextTime(
                                            new CronExpression(jobInfo.getJobCron())
                                                    .getNextValidTimeAfter(new Date())
                                                    .getTime()
                                    );
                                } else {    // 未过期：正常触发，递增计算下次触发时间
                                    ringSecond = (int)((jobInfo.getTriggerNextTime()/1000)%60);

                                    jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
                                    jobInfo.setTriggerNextTime(
                                            new CronExpression(jobInfo.getJobCron())
                                                    .getNextValidTimeAfter(new Date(jobInfo.getTriggerNextTime()))
                                                    .getTime()
                                    );
                                }
                                if (ringSecond == -1) {
                                    continue;
                                }

                                // push async ring
                                List<Integer> ringItemData = ringData.get(ringSecond);
                                if (ringItemData == null) {
                                    ringItemData = new ArrayList<Integer>();
                                    ringData.put(ringSecond, ringItemData);
                                }
                                ringItemData.add(jobInfo.getId());

                                logger.debug(">>>>>>>>>>> xxl-job, push time-ring : " + ringSecond + " = " + Arrays.asList(ringItemData) );
                            }

                            // 3、更新trigger信息
                            for (XxlJobInfo jobInfo: scheduleList) {
                                XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleUpdate(jobInfo);
                            }

                        }

                        // tx stop

                        conn.commit();
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread error:{}", e);
                        }
                    } finally {
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                            }
                        }
                        if (null != preparedStatement) {
                            try {
                                preparedStatement.close();
                            } catch (SQLException ignore) {
                            }
                        }
                    }
                    long cost = System.currentTimeMillis()-start;

                    // next second, align second
                    try {
                        if (cost < 1000) {
                            TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis()%1000);
                        }
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                }
                logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread stop");
            }
        });
        scheduleThread.setDaemon(true);
        scheduleThread.setName("xxl-job, admin JobScheduleHelper#scheduleThread");
        scheduleThread.start();


        // ring thread
        ringThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // align second
                try {
                    TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis()%1000 );
                } catch (InterruptedException e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }

                int lastSecond = -1;
                while (!toStop) {

                    try {
                        // second data
                        List<Integer> ringItemData = new ArrayList<>();
                        int nowSecond = (int)((System.currentTimeMillis()/1000)%60);   // 避免处理耗时太长，跨过刻度；
                        if (lastSecond == -1) {
                            lastSecond = (nowSecond+59)%60;
                        }
                        for (int i = 1; i <=60; i++) {
                            int secondItem = (lastSecond+i)%60;

                            List<Integer> tmpData = ringData.remove(secondItem);
                            if (tmpData != null) {
                                ringItemData.addAll(tmpData);
                            }

                            if (secondItem == nowSecond) {
                                break;
                            }
                        }
                        lastSecond = nowSecond;

                        // ring trigger
                        logger.debug(">>>>>>>>>>> xxl-job, time-ring beat : " + nowSecond + " = " + Arrays.asList(ringItemData) );
                        if (ringItemData!=null && ringItemData.size()>0) {
                            // do trigger
                            for (int jobId: ringItemData) {
                                // do trigger
                                JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.CRON, -1, null, null);
                            }
                            // clear
                            ringItemData.clear();
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread error:{}", e);
                        }
                    }

                    // next second, align second
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis()%1000);
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread stop");
            }
        });
        ringThread.setDaemon(true);
        ringThread.setName("xxl-job, admin JobScheduleHelper#ringThread");
        ringThread.start();
    }

    public void toStop(){
        toStop = true;

        // interrupt and wait
        scheduleThread.interrupt();
        try {
            scheduleThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        // interrupt and wait
        ringThread.interrupt();
        try {
            ringThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
