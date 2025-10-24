package com.xxl.job.admin.scheduler.thread;

import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.scheduler.misfire.MisfireStrategyEnum;
import com.xxl.job.admin.scheduler.type.ScheduleTypeEnum;
import com.xxl.job.admin.scheduler.trigger.TriggerTypeEnum;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.MapTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2019-05-21
 */
public class JobScheduleHelper {
    private static final Logger logger = LoggerFactory.getLogger(JobScheduleHelper.class);


    public static final long PRE_READ_MS = 5000;    // pre read

    private Thread scheduleThread;
    private Thread ringThread;
    private volatile boolean scheduleThreadToStop = false;
    private volatile boolean ringThreadToStop = false;
    private final Map<Integer, List<Integer>> ringData = new ConcurrentHashMap<>();

    /**
     * start
     */
    public void start(){

        // schedule thread
        scheduleThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // align time
                try {
                    TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis()%1000 );
                } catch (Throwable e) {
                    if (!scheduleThreadToStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.info(">>>>>>>>> init xxl-job admin scheduler success.");

                // pre-read count: treadpool-size * trigger-qps (each trigger cost 100ms, qps = 1000/100 = 100)
                int preReadCount = (XxlJobAdminBootstrap.getInstance().getTriggerPoolFastMax() + XxlJobAdminBootstrap.getInstance().getTriggerPoolSlowMax()) * 10;

                // do schedule
                while (!scheduleThreadToStop) {

                    // param
                    long start = System.currentTimeMillis();
                    boolean preReadSuc = true;

                    // transaction start
                    TransactionStatus transactionStatus = XxlJobAdminBootstrap.getInstance().getTransactionManager().getTransaction(new DefaultTransactionDefinition());
                    try {
                        // 1、job lock
                        String lockedRecord = XxlJobAdminBootstrap.getInstance().getXxlJobLockMapper().scheduleLock();
                        long nowTime = System.currentTimeMillis();

                        // scan and process job
                        List<XxlJobInfo> scheduleList = XxlJobAdminBootstrap.getInstance().getXxlJobInfoMapper().scheduleJobQuery(nowTime + PRE_READ_MS, preReadCount);
                        if (CollectionTool.isNotEmpty(scheduleList)) {

                            // 2、push time-ring
                            for (XxlJobInfo jobInfo: scheduleList) {

                                // time-ring jump
                                if (nowTime > jobInfo.getTriggerNextTime() + PRE_READ_MS) {
                                    // 2.1、trigger-expire > 5s：pass && make next-trigger-time

                                    // 1、misfire handle
                                    MisfireStrategyEnum misfireStrategyEnum = MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), MisfireStrategyEnum.DO_NOTHING);
                                    misfireStrategyEnum.getMisfireHandler().handle(jobInfo.getId());

                                    // 2、fresh next
                                    refreshNextTriggerTime(jobInfo, new Date());

                                } else if (nowTime > jobInfo.getTriggerNextTime()) {
                                    // 2.2、trigger-expire < 5s：direct-trigger && make next-trigger-time

                                    // 1、trigger direct
                                    XxlJobAdminBootstrap.getInstance().getJobTriggerPoolHelper().trigger(jobInfo.getId(), TriggerTypeEnum.CRON, -1, null, null, null);
                                    logger.debug(">>>>>>>>>>> xxl-job, schedule expire, direct trigger : jobId = " + jobInfo.getId() );

                                    // 2、fresh next
                                    refreshNextTriggerTime(jobInfo, new Date());

                                    // next-trigger-time in 5s, pre-read again
                                    if (jobInfo.getTriggerStatus()==1 && nowTime + PRE_READ_MS > jobInfo.getTriggerNextTime()) {

                                        // 1、make ring second
                                        int ringSecond = (int)((jobInfo.getTriggerNextTime()/1000)%60);

                                        // 2、push time ring (pre read)
                                        pushTimeRing(ringSecond, jobInfo.getId());
                                        logger.debug(">>>>>>>>>>> xxl-job, schedule pre-read, push trigger : jobId = " + jobInfo.getId() );

                                        // 3、fresh next
                                        refreshNextTriggerTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));

                                    }

                                } else {
                                    // 2.3、trigger-pre-read：time-ring trigger && make next-trigger-time

                                    // 1、make ring second
                                    int ringSecond = (int)((jobInfo.getTriggerNextTime()/1000)%60);

                                    // 2、push time ring
                                    pushTimeRing(ringSecond, jobInfo.getId());
                                    logger.debug(">>>>>>>>>>> xxl-job, schedule normal, push trigger : jobId = " + jobInfo.getId() );

                                    // 3、fresh next
                                    refreshNextTriggerTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));

                                }

                            }

                            // 3、update trigger info
                            for (XxlJobInfo jobInfo: scheduleList) {
                                XxlJobAdminBootstrap.getInstance().getXxlJobInfoMapper().scheduleUpdate(jobInfo);
                            }

                        } else {
                            preReadSuc = false;
                        }

                    } catch (Throwable e) {
                        if (!scheduleThreadToStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread error:{}", e.getMessage(), e);
                        }
                    } finally {
                        // transaction commit
                        XxlJobAdminBootstrap.getInstance().getTransactionManager().commit(transactionStatus);   // avlid schedule repeat
                    }
                    // transaction end
                    long cost = System.currentTimeMillis()-start;


                    // Wait seconds, align second
                    if (cost < 1000) {  // scan-overtime, not wait
                        try {
                            // pre-read period: success > scan each second; fail > skip this period;
                            TimeUnit.MILLISECONDS.sleep((preReadSuc?1000:PRE_READ_MS) - System.currentTimeMillis()%1000);
                        } catch (Throwable e) {
                            if (!scheduleThreadToStop) {
                                logger.error(e.getMessage(), e);
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

                while (!ringThreadToStop) {

                    // align second
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
                    } catch (Throwable e) {
                        if (!ringThreadToStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    try {
                        // second data
                        List<Integer> ringItemData = new ArrayList<>();

                        // collect rind data, by second
                        int nowSecond = Calendar.getInstance().get(Calendar.SECOND);
                        for (int i = 0; i <= 2; i++) {                                                              // 避免调度遗漏：处理耗时太长、跨过刻度，除当前刻度外 + 向前校验2个刻度；
                            List<Integer> ringItemList = ringData.remove( (nowSecond+60-i)%60 );
                            if (CollectionTool.isNotEmpty(ringItemList)) {
                                // distinct for each second
                                List<Integer> ringItemListDistinct = ringItemList.stream().distinct().toList();     // 避免调度重复：重复推送时间轮刻度，去重只保留一个；；
                                if (ringItemListDistinct.size() < ringItemList.size()) {
                                    logger.warn(">>>>>>>>>>> xxl-job, time-ring found job repeat beat : " + nowSecond + " = " + ringItemData);
                                }

                                // collect ring item
                                ringItemData.addAll(ringItemListDistinct);
                            }
                        }

                        // ring trigger
                        logger.debug(">>>>>>>>>>> xxl-job, time-ring beat : " + nowSecond + " = " + ringItemData);
                        if (CollectionTool.isNotEmpty(ringItemData)) {
                            // do trigger
                            for (int jobId: ringItemData) {
                                // do trigger
                                XxlJobAdminBootstrap.getInstance().getJobTriggerPoolHelper().trigger(jobId, TriggerTypeEnum.CRON, -1, null, null, null);
                            }
                            // clear
                            ringItemData.clear();
                        }
                    } catch (Throwable e) {
                        if (!ringThreadToStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread error:{}", e.getMessage(), e);
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

    /**
     * refresh next trigger time of job
     *
     * @param jobInfo   job info
     * @param fromTime  from time
     */
    private void refreshNextTriggerTime(XxlJobInfo jobInfo, Date fromTime) {
        try {
            // generate next trigger time
            ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), ScheduleTypeEnum.NONE);
            Date nextTriggerTime = scheduleTypeEnum.getScheduleType().generateNextTriggerTime(jobInfo, fromTime);

            // refresh next trigger-time + status
            if (nextTriggerTime != null) {
                // generate success
                jobInfo.setTriggerStatus(-1);                               // pass, may be Inaccurate
                jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
                jobInfo.setTriggerNextTime(nextTriggerTime.getTime());
            } else {
                // generate fail, stop job
                jobInfo.setTriggerStatus(0);
                jobInfo.setTriggerLastTime(0);
                jobInfo.setTriggerNextTime(0);
                logger.error(">>>>>>>>>>> xxl-job, refreshNextValidTime fail for job: jobId={}, scheduleType={}, scheduleConf={}",
                        jobInfo.getId(), jobInfo.getScheduleType(), jobInfo.getScheduleConf());
            }
        } catch (Throwable e) {
            // generate error, stop job
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(0);
            jobInfo.setTriggerNextTime(0);

            logger.error(">>>>>>>>>>> xxl-job, refreshNextValidTime error for job: jobId={}, scheduleType={}, scheduleConf={}",
                    jobInfo.getId(), jobInfo.getScheduleType(), jobInfo.getScheduleConf(), e);
        }
    }

    /**
     * push time ring
     *
     * @param ringSecond    ring second
     * @param jobId         job id
     */
    private void pushTimeRing(int ringSecond, int jobId){
        // get ringItemData, init when not exists
        List<Integer> ringItemList = ringData.computeIfAbsent(
                ringSecond,
                k -> new ArrayList<>());

        // push async rind
        ringItemList.add(jobId);
        logger.debug(">>>>>>>>>>> xxl-job, schedule push time-ring : " + ringSecond + " = " + List.of(ringItemList));
    }

    /**
     * stop
     */
    public void stop(){

        // 1、stop schedule
        scheduleThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);  // wait
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
        if (scheduleThread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            scheduleThread.interrupt();
            try {
                scheduleThread.join();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }

        // if has ring data
        boolean hasRingData = false;
        if (MapTool.isNotEmpty(ringData)) {
            for (int second : ringData.keySet()) {
                List<Integer> ringItemList = ringData.get(second);
                if (CollectionTool.isNotEmpty(ringItemList)) {
                    hasRingData = true;
                    break;
                }
            }
        }
        if (hasRingData) {
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }

        // stop ring (wait job-in-memory stop)
        ringThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
        if (ringThread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            ringThread.interrupt();
            try {
                ringThread.join();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }

        logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper stop");
    }

}
