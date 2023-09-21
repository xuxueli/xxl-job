package com.xxl.job.admin.thread;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.admin.common.config.XxlJobAdminProperties;
import com.xxl.job.admin.common.enums.MisfireStrategyEnum;
import com.xxl.job.admin.common.enums.TriggerTypeEnum;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.common.utils.CronUtils;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.core.thread.AbstractThreadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度线程
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class ScheduleThread extends AbstractThreadListener implements Ordered {

    @Autowired
    private XxlJobAdminProperties xxlJobAdminProperties;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private TriggerThreadPool jobTriggerThreadPool;

    /**
     * pre read
     */
    public static final long PRE_READ_MS = 5000;

    private Thread scheduleThread;
    private Thread ringThread;
    private volatile boolean scheduleThreadToStop = false;
    private volatile boolean ringThreadToStop = false;
    private volatile static Map<Integer, List<Long>> ringData = new ConcurrentHashMap<>();

    @Override
    public int getOrder() {
        return 6;
    }

    @Override
    public void start() {

        // schedule thread
        scheduleThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis() % 1000);
                } catch (InterruptedException e) {
                    if (!scheduleThreadToStop) {
                        log.error(e.getMessage(), e);
                    }
                }
                log.info(">>>>>>>>> init xxl-job admin scheduler success.");

                // pre-read count: treadpool-size * trigger-qps (each trigger cost 50ms, qps = 1000/50 = 20)
                int preReadCount = (xxlJobAdminProperties.getTriggerPoolFastMax() + xxlJobAdminProperties.getTriggerPoolSlowMax()) * 20;

                while (!scheduleThreadToStop) {

                    // Scan Job
                    long start = System.currentTimeMillis();

                    Connection conn = null;
                    Boolean connAutoCommit = null;
                    PreparedStatement preparedStatement = null;

                    boolean preReadSuc = true;
                    try {

                        conn = dataSource.getConnection();
                        connAutoCommit = conn.getAutoCommit();
                        conn.setAutoCommit(false);

                        preparedStatement = conn.prepareStatement("select * from xxl_job_lock where LOCK_NAME = 'schedule_lock' for update");
                        preparedStatement.execute();

                        // tx start

                        // 1、pre read
                        long nowTime = System.currentTimeMillis();
                        List<JobInfoVO> scheduleList = jobInfoService.queryJobInfoByTriggerNextTime(DateUtil.date(nowTime + PRE_READ_MS), preReadCount);
                        if (CollectionUtil.isNotEmpty(scheduleList)) {
                            // 2、push time-ring
                            for (JobInfoVO jobInfo : scheduleList) {

                                // time-ring jump
                                if (nowTime > jobInfo.getTriggerNextTime().getTime() + PRE_READ_MS) {
                                    // 2.1、trigger-expire > 5s：pass && make next-trigger-time
                                    log.warn(">>>>>>>>>>> xxl-job, schedule misfire, jobId = " + jobInfo.getId());

                                    // 1、misfire match
                                    MisfireStrategyEnum misfireStrategyEnum = MisfireStrategyEnum.match(jobInfo.getMisfireStrategy());
                                    if (MisfireStrategyEnum.FIRE_ONCE_NOW.equals(misfireStrategyEnum)) {
                                        // FIRE_ONCE_NOW 》 trigger
                                        jobTriggerThreadPool.addTrigger(jobInfo.getId(), TriggerTypeEnum.MISFIRE, -1,
                                                null, null, null);
                                        log.debug(">>>>>>>>>>> xxl-job, schedule push trigger : jobId = " + jobInfo.getId());
                                    }

                                    // 2、fresh next
                                    refreshNextValidTime(jobInfo, DateUtil.date());

                                } else if (nowTime > jobInfo.getTriggerNextTime().getTime()) {
                                    // 2.2、trigger-expire < 5s：direct-trigger && make next-trigger-time

                                    // 1、trigger
                                    jobTriggerThreadPool.addTrigger(jobInfo.getId(),
                                            TriggerTypeEnum.CRON, -1,
                                            null, null, null);
                                    log.debug(">>>>>>>>>>> xxl-job, schedule push trigger : jobId = " + jobInfo.getId());

                                    // 2、fresh next
                                    refreshNextValidTime(jobInfo, DateUtil.date());

                                    // next-trigger-time in 5s, pre-read again
                                    if (jobInfo.getTriggerStatus() == 1 && nowTime + PRE_READ_MS > jobInfo.getTriggerNextTime().getTime()) {

                                        // 1、make ring second
                                        int ringSecond = (int) ((jobInfo.getTriggerNextTime().getTime() / 1000) % 60);

                                        // 2、push time ring
                                        pushTimeRing(ringSecond, jobInfo.getId());

                                        // 3、fresh next
                                        refreshNextValidTime(jobInfo, jobInfo.getTriggerNextTime());

                                    }

                                } else {
                                    // 2.3、trigger-pre-read：time-ring trigger && make next-trigger-time

                                    // 1、make ring second
                                    int ringSecond = (int) ((jobInfo.getTriggerNextTime().getTime() / 1000) % 60);

                                    // 2、push time ring
                                    pushTimeRing(ringSecond, jobInfo.getId());

                                    // 3、fresh next
                                    refreshNextValidTime(jobInfo, jobInfo.getTriggerNextTime());

                                }

                            }

                            // 3、update trigger info
                            for (JobInfoVO jobInfo : scheduleList) {
                                jobInfoService.updateTriggerTimeById(jobInfo.getId(), jobInfo.getTriggerLastTime(),
                                        jobInfo.getTriggerNextTime(), jobInfo.getTriggerStatus());
                            }

                        } else {
                            preReadSuc = false;
                        }

                        // tx stop


                    } catch (Exception e) {
                        if (!scheduleThreadToStop) {
                            log.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread error:{}", e);
                        }
                    } finally {

                        // commit
                        if (ObjectUtil.isNotNull(conn)) {
                            try {
                                conn.commit();
                            } catch (SQLException e) {
                                if (!scheduleThreadToStop) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                            try {
                                conn.setAutoCommit(connAutoCommit);
                            } catch (SQLException e) {
                                if (!scheduleThreadToStop) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                if (!scheduleThreadToStop) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                        }

                        // close PreparedStatement
                        if (ObjectUtil.isNotNull(preparedStatement)) {
                            try {
                                preparedStatement.close();
                            } catch (SQLException e) {
                                if (!scheduleThreadToStop) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                    long cost = System.currentTimeMillis() - start;


                    // Wait seconds, align second
                    if (cost < 1000) {  // scan-overtime, not wait
                        try {
                            // pre-read period: success > scan each second; fail > skip this period;
                            TimeUnit.MILLISECONDS.sleep((preReadSuc ? 1000 : PRE_READ_MS) - System.currentTimeMillis() % 1000);
                        } catch (InterruptedException e) {
                            if (!scheduleThreadToStop) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }

                }

                log.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread stop");
            }
        });
        scheduleThread.setDaemon(true);
        scheduleThread.setName("xxl-job, admin JobScheduleHelper#scheduleThread");
        scheduleThread.start();


        // ring thread
        ringThread = new Thread(new Runnable() {
            @Override
            public void run() {

                log.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread start...");

                while (!ringThreadToStop) {

                    // align second
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
                    } catch (InterruptedException e) {
                        if (!ringThreadToStop) {
                            log.error(e.getMessage(), e);
                        }
                    }

                    try {
                        // second data
                        List<Long> ringItemData = new ArrayList<>();
                        int nowSecond = Calendar.getInstance().get(Calendar.SECOND);   // 避免处理耗时太长，跨过刻度，向前校验一个刻度；
                        for (int i = 0; i < 2; i++) {
                            List<Long> tmpData = ringData.remove((nowSecond + 60 - i) % 60);
                            if (tmpData != null) {
                                ringItemData.addAll(tmpData);
                            }
                        }

                        // ring trigger
                        log.debug(">>>>>>>>>>> xxl-job, time-ring beat : " + nowSecond + " = " + Arrays.asList(ringItemData));

                        if (CollectionUtil.isNotEmpty(ringItemData)) {
                            // do trigger
                            for (Long jobId : ringItemData) {
                                // do trigger
                                jobTriggerThreadPool.addTrigger(jobId, TriggerTypeEnum.CRON,
                                        -1, null,
                                        null, null);
                            }
                            // clear
                            ringItemData.clear();
                        }
                    } catch (Exception e) {
                        if (!ringThreadToStop) {
                            log.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread error:{}", e);
                        }
                    }
                }
                log.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread stop");
            }
        });
        ringThread.setDaemon(true);
        ringThread.setName("xxl-job, admin JobScheduleHelper#ringThread");
        ringThread.start();
    }

    @Override
    public void stop() {

        // 1、stop schedule
        scheduleThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        if (scheduleThread.getState() != Thread.State.TERMINATED) {
            // interrupt and wait
            scheduleThread.interrupt();
            try {
                scheduleThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        // if has ring data
        boolean hasRingData = false;
        if (!ringData.isEmpty()) {
            for (int second : ringData.keySet()) {
                List<Long> tmpData = ringData.get(second);
                if (CollectionUtil.isNotEmpty(tmpData)) {
                    hasRingData = true;
                    break;
                }
            }
        }
        if (hasRingData) {
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        // stop ring (wait job-in-memory stop)
        ringThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        if (ringThread.getState() != Thread.State.TERMINATED) {
            // interrupt and wait
            ringThread.interrupt();
            try {
                ringThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        log.info(">>>>>>>>>>> xxl-job, JobScheduleHelper stop");
    }

    private void refreshNextValidTime(JobInfoVO jobInfo, Date fromTime) {
        Date nextValidTime = CronUtils.generateNextValidTime(jobInfo.getScheduleType(), jobInfo.getScheduleConf(), fromTime);
        if (nextValidTime != null) {
            jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
            jobInfo.setTriggerNextTime(nextValidTime);
        } else {
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(DateUtil.date(1));
            jobInfo.setTriggerNextTime(DateUtil.date(1));
            log.warn(">>>>>>>>>>> xxl-job, refreshNextValidTime fail for job: jobId={}, scheduleType={}, scheduleConf={}",
                    jobInfo.getId(), jobInfo.getScheduleType(), jobInfo.getScheduleConf());
        }
    }

    private void pushTimeRing(int ringSecond, Long jobId) {
        // push async ring
        List<Long> ringItemData = ringData.get(ringSecond);
        if (CollectionUtil.isEmpty(ringItemData)) {
            ringItemData = CollectionUtil.newArrayList();
            ringData.put(ringSecond, ringItemData);
        }
        ringItemData.add(jobId);

        log.debug(">>>>>>>>>>> xxl-job, schedule push time-ring : " + ringSecond + " = " + Collections.singletonList(ringItemData));
    }



}
