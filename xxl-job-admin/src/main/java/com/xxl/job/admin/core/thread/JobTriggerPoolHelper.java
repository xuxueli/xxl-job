package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.trigger.XxlJobTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * job trigger thread pool helper
 *
 * @author xuxueli 2018-07-03 21:08:07
 */
public class JobTriggerPoolHelper {
    private static Logger logger = LoggerFactory.getLogger(JobTriggerPoolHelper.class);


    // ---------------------- trigger pool ----------------------

    private ThreadPoolExecutor triggerPool = new ThreadPoolExecutor(
            20,
            200,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100000),
            new ThreadPoolExecutor.CallerRunsPolicy());


    public void addTrigger(final int jobId, final int failRetryCount, final TriggerTypeEnum triggerType){
        triggerPool.execute(new Runnable() {
            @Override
            public void run() {
                XxlJobTrigger.trigger(jobId, failRetryCount, triggerType);
            }
        });
    }

    public void stop(){
        //triggerPool.shutdown();
        triggerPool.shutdownNow();
        logger.info(">>>>>>>>> xxl-job trigger thread pool shutdown success.");
    }

    // ---------------------- helper ----------------------

    private static JobTriggerPoolHelper helper = new JobTriggerPoolHelper();

    /**
     *
     * @param jobId
     * @param failRetryCount
     * 			>=0: use this param
     * 			<0: use param from job info config
     *
     */
    public static void trigger(int jobId, int failRetryCount, TriggerTypeEnum triggerType) {
        helper.addTrigger(jobId, failRetryCount, triggerType);
    }

    public static void toStop(){
        helper.stop();
    }

}
