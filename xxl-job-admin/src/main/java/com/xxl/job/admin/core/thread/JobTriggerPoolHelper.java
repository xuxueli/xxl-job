package com.xxl.job.admin.core.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.trigger.XxlJobTrigger;
import com.xxl.job.core.util.XxlJobTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * job trigger thread pool helper
 *
 * @author xuxueli 2018-07-03 21:08:07
 */
public class JobTriggerPoolHelper {

	private static final Logger logger = LoggerFactory.getLogger(JobTriggerPoolHelper.class);

	// ---------------------- trigger pool ----------------------

	// fast/slow thread pool
	ThreadPoolExecutor fastTriggerPool;
	ThreadPoolExecutor slowTriggerPool;
	// job timeout count
	volatile long minTim = System.currentTimeMillis() / 60000; // ms -> min
	final ConcurrentMap<Integer, AtomicInteger> jobTimeoutCountMap = new ConcurrentHashMap<>();

	public void start() {
		XxlJobAdminConfig config = XxlJobAdminConfig.getAdminConfig();

		fastTriggerPool = createExecutor(config.getTriggerPoolFastMax(), 1000, "xxl-job-fastTriggerPool-");
		slowTriggerPool = createExecutor(config.getTriggerPoolSlowMax(), 2000, "xxl-job-slowTriggerPool-");
	}

	static ThreadPoolExecutor createExecutor(int maxPoolSize, int queueCapacity, String namePrefix) {
		return new ThreadPoolExecutor(10, maxPoolSize, 60L, TimeUnit.SECONDS
				, new LinkedBlockingQueue<>(queueCapacity)
				, XxlJobTool.namedThreadFactory(namePrefix));
	}

	public void stop() {
		fastTriggerPool.shutdownNow();
		slowTriggerPool.shutdownNow();
		logger.info(">>>>>>>>> xxl-job trigger thread pool shutdown success.");
	}

	/**
	 * add trigger
	 */
	public void addTrigger(final Integer jobId,
	                       final TriggerTypeEnum triggerType,
	                       final int failRetryCount,
	                       final String executorShardingParam,
	                       final String executorParam,
	                       final String addressList) {

		// choose thread pool
		ThreadPoolExecutor triggerPool_ = fastTriggerPool;
		AtomicInteger jobTimeoutCount = jobTimeoutCountMap.get(jobId);
		if (jobTimeoutCount != null && jobTimeoutCount.get() > 10) {      // job-timeout 10 times in 1 min
			triggerPool_ = slowTriggerPool;
		}

		// trigger
		triggerPool_.execute(() -> {
			long start = System.currentTimeMillis();

			try {
				// do trigger
				XxlJobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			} finally {
				// check timeout-count-map
				long minTim_now = System.currentTimeMillis() / 60000;
				if (minTim != minTim_now) {
					minTim = minTim_now;
					jobTimeoutCountMap.clear();
				}

				// incr timeout-count-map
				long cost = System.currentTimeMillis() - start;
				if (cost > 500) {       // ob-timeout threshold 500ms
					AtomicInteger timeoutCount = jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
					if (timeoutCount != null) {
						timeoutCount.incrementAndGet();
					}
				}

			}

		});
	}

	// ---------------------- helper ----------------------

	static final JobTriggerPoolHelper helper = new JobTriggerPoolHelper();

	public static void toStart() {
		helper.start();
	}

	public static void toStop() {
		helper.stop();
	}

	/**
	 * @param failRetryCount >=0: use this param
	 * <0: use param from job info config
	 * @param executorParam null: use job param
	 * not null: cover job param
	 */
	public static void trigger(int jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam, String addressList) {
		helper.addTrigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
	}

}