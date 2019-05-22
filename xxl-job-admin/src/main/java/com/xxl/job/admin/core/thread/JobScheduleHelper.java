package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.cron.CronExpression;
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
                while (!toStop) {
                    // 随机休眠1s内
                    try {
                        TimeUnit.MILLISECONDS.sleep(500+new Random().nextInt(500));
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }

                    // 匹配任务
                    Connection conn = null;
                    PreparedStatement preparedStatement = null;
                    try {
                        if (conn==null || conn.isClosed()) {
                            conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
                        }
                        conn.setAutoCommit(false);

                        preparedStatement = conn.prepareStatement(  "select * from XXL_JOB_LOCK where lock_name = 'schedule_lock' for update" );
                        preparedStatement.execute();

                        // tx start

                        // 1、查询JOB："下次调度30s内"  ( ...... -5 ... now ...... +30 ...... )
                        long maxNextTime = System.currentTimeMillis() + 30000;
                        long nowTime = System.currentTimeMillis();
                        List<XxlJobInfo> scheduleList = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleJobQuery(maxNextTime);
                        if (scheduleList!=null && scheduleList.size()>0) {
                            // 2、推送时间轮
                            for (XxlJobInfo jobInfo: scheduleList) {

                                // 过期策略：过期=更新下次触发时间为当前、过期是否5s内=立即出发，否则忽略；
                                if (jobInfo.getTriggerNextTime() < nowTime) {
                                    jobInfo.setTriggerNextTime(nowTime);
                                    if (jobInfo.getTriggerNextTime() < nowTime-10000) {
                                        continue;
                                    }
                                }

                                // push async ring
                                int second = (int)((jobInfo.getTriggerNextTime()/1000)%60);
                                List<Integer> ringItemData = ringData.get(second);
                                if (ringItemData == null) {
                                    ringItemData = new ArrayList<Integer>();
                                    ringData.put(second, ringItemData);
                                }
                                ringItemData.add(jobInfo.getId());

                                logger.info(">>>>>>>>>>> xxl-job, push time-ring : " + second + " = " + Arrays.asList(ringItemData) );
                            }

                            // 3、更新trigger信息
                            for (XxlJobInfo jobInfo: scheduleList) {
                                // update
                                jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
                                jobInfo.setTriggerNextTime(
                                        new CronExpression(jobInfo.getJobCron())
                                                .getNextValidTimeAfter(new Date(jobInfo.getTriggerNextTime()))
                                                .getTime()
                                );
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
                int lastSecond = -1;
                while (!toStop) {
                    try {
                        // second data
                        List<Integer> ringItemData = new ArrayList<>();
                        int nowSecond = (int)((System.currentTimeMillis()/1000)%60);   // 避免处理耗时太长，跨过刻度；
                        if (lastSecond == -1) {
                            if (ringData.containsKey(nowSecond)) {
                                List<Integer> tmpData = ringData.remove(nowSecond);
                                if (tmpData != null) {
                                    ringItemData.addAll(tmpData);
                                }
                            }
                            lastSecond = nowSecond;
                        } else {
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
                        }

                        logger.info(">>>>>>>>>>> xxl-job, time-ring beat : " + nowSecond + " = " + Arrays.asList(ringItemData) );
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

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
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
