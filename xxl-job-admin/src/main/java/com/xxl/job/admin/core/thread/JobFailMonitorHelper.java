package com.xxl.job.admin.core.thread;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * job monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobFailMonitorHelper {

	private static final Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);

	private static final JobFailMonitorHelper instance = new JobFailMonitorHelper();

	public static JobFailMonitorHelper getInstance() {
		return instance;
	}

	// ---------------------- monitor ----------------------

	private Thread monitorThread;
	private volatile boolean toStop = false;
	private volatile Long lastId = 0L;

	public void start() {
		monitorThread = new Thread(() -> {
			// monitor
			while (!toStop) {
				try {
					final XxlJobAdminConfig adminConfig = XxlJobAdminConfig.getAdminConfig();
					final XxlJobLogDao xxlJobLogDao = adminConfig.getXxlJobLogDao();
					List<Long> failLogIds = xxlJobLogDao.findFailJobLogIds(1000, lastId);
					if (failLogIds != null && !failLogIds.isEmpty()) {
						for (Long failLogId : failLogIds) {
							// lock log
							int lockRet = xxlJobLogDao.updateAlarmStatus(failLogId, 0, -1);
							if (lockRet < 1) {
								continue;
							}
							XxlJobLog log = xxlJobLogDao.load(failLogId);
							XxlJobInfo info = adminConfig.getXxlJobInfoDao().loadById(log.getJobId());

							// 1、fail retry monitor
							if (log.getExecutorFailRetryCount() > 0) {
								JobTriggerPoolHelper.trigger(log.getJobId(), TriggerTypeEnum.RETRY, (log.getExecutorFailRetryCount() - 1), log.getExecutorShardingParam(), log.getExecutorParam(), null);
								String retryMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_type_retry") + "<<<<<<<<<<< </span><br>";
								log.setTriggerMsg(log.getTriggerMsg() + retryMsg);
								xxlJobLogDao.updateTriggerInfo(log);
							}

							// 2、fail alarm monitor
							int newAlarmStatus; // 告警状态：0-默认、-1=锁定状态、1-无需告警、2-告警成功、3-告警失败
							if (info != null) {
								boolean alarmResult = adminConfig.getJobAlarmer().alarm(info, log);
								newAlarmStatus = alarmResult ? 2 : 3;
							} else {
								newAlarmStatus = 1;
							}

							xxlJobLogDao.updateAlarmStatus(failLogId, -1, newAlarmStatus);
						}
						lastId = failLogIds.get(failLogIds.size() - 1);
					}

				} catch (Exception e) {
					if (!toStop) {
						logger.error(">>>>>>>>>>> xxl-job, job fail monitor thread error", e);
					}
				}

				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (Exception e) {
					if (!toStop) {
						logger.error(e.getMessage(), e);
					}
				}

			}

			logger.info(">>>>>>>>>>> xxl-job, job fail monitor thread stop");

		});
		monitorThread.setDaemon(true);
		monitorThread.setName("xxl-job, admin JobFailMonitorHelper");
		monitorThread.start();
	}

	public void toStop() {
		toStop = true;
		// interrupt and wait
		final Thread thread = monitorThread;
		if (thread != null) {
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}