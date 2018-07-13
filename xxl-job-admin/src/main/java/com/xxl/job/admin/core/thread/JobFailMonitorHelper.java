package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.alarm.Alarm;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * job monitor instance
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobFailMonitorHelper {
	private static Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);

	private static final Set<Alarm> ALARM_SET = new HashSet<>();
	private static JobFailMonitorHelper instance = new JobFailMonitorHelper();
	public static JobFailMonitorHelper getInstance(){
		return instance;
	}
	static {
		ServiceLoader<Alarm> serviceLoader = ServiceLoader.load(Alarm.class);
		for (Alarm alarm : serviceLoader) {
			ALARM_SET.add(alarm);
		}
		logger.info("load alarm service {}, {}", ALARM_SET.size(), ALARM_SET);
	}
	// ---------------------- monitor ----------------------

	private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(0xfff8);

	private Thread monitorThread;
	private volatile boolean toStop = false;
	public void start(){
		monitorThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// monitor
				while (!toStop) {
					try {
						List<Integer> jobLogIdList = new ArrayList<Integer>();
						int drainToNum = JobFailMonitorHelper.instance.queue.drainTo(jobLogIdList);

						if (CollectionUtils.isNotEmpty(jobLogIdList)) {
							for (Integer jobLogId : jobLogIdList) {
								if (jobLogId==null || jobLogId==0) {
									continue;
								}
								XxlJobLog log = XxlJobDynamicScheduler.xxlJobLogDao.load(jobLogId);
								if (log == null) {
									continue;
								}
								if (IJobHandler.SUCCESS.getCode() == log.getTriggerCode() && log.getHandleCode() == 0) {
									// job running
									JobFailMonitorHelper.monitor(jobLogId);
									logger.info(">>>>>>>>>>> job monitor, job running, JobLogId:{}", jobLogId);
								} else if (IJobHandler.SUCCESS.getCode() == log.getHandleCode()) {
									// job success, pass
									logger.info(">>>>>>>>>>> job monitor, job success, JobLogId:{}", jobLogId);
								} else /*if (IJobHandler.FAIL.getCode() == log.getTriggerCode()
										|| IJobHandler.FAIL.getCode() == log.getHandleCode()
										|| IJobHandler.FAIL_RETRY.getCode() == log.getHandleCode() )*/ {
									// job fail,
									failAlarm(log);
									logger.info(">>>>>>>>>>> job monitor, job fail, JobLogId:{}", jobLogId);
								}/* else {
									JobFailMonitorHelper.monitor(jobLogId);
									logger.info(">>>>>>>>>>> job monitor, job status unknown, JobLogId:{}", jobLogId);
								}*/
							}
						}

						TimeUnit.SECONDS.sleep(10);
					} catch (Exception e) {
						logger.error("job monitor error:{}", e);
					}
				}

				// monitor all clear
				List<Integer> jobLogIdList = new ArrayList<Integer>();
				int drainToNum = getInstance().queue.drainTo(jobLogIdList);
				if (jobLogIdList!=null && jobLogIdList.size()>0) {
					for (Integer jobLogId: jobLogIdList) {
						XxlJobLog log = XxlJobDynamicScheduler.xxlJobLogDao.load(jobLogId);
						if (ReturnT.FAIL_CODE == log.getTriggerCode()|| ReturnT.FAIL_CODE==log.getHandleCode()) {
							// job fail,
							failAlarm(log);
							logger.info(">>>>>>>>>>> job monitor last, job fail, JobLogId:{}", jobLogId);
						}
					}
				}

			}
		});
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	public void toStop(){
		toStop = true;
		// interrupt and wait
		monitorThread.interrupt();
		try {
			monitorThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	// producer
	public static void monitor(int jobLogId){
		getInstance().queue.offer(jobLogId);
	}

	/**
	 * fail alarm
	 *
	 * @param jobLog {@link XxlJobLog}
	 */
	private void failAlarm(XxlJobLog jobLog){

		XxlJobInfo info = XxlJobDynamicScheduler.xxlJobInfoDao.loadById(jobLog.getJobId());

		if (null == info || StringUtils.isBlank(info.getAlarmEmail())) {
			return;
		}

		XxlJobGroup group = XxlJobDynamicScheduler.xxlJobGroupDao.load(info.getJobGroup());

		//trigger all the alarm service
		for (Alarm alarm : ALARM_SET) {
			alarm.sendAlarm(info, jobLog, group);
		}

	}

}
