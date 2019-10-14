package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.alarm.AlarmFactory;
import com.xxl.job.admin.core.alarm.AlarmWay;
import com.xxl.job.admin.core.alarm.IAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * job monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobFailMonitorHelper {
	private static Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);
	
	private static JobFailMonitorHelper instance = new JobFailMonitorHelper();
	public static JobFailMonitorHelper getInstance(){
		return instance;
	}

	// ---------------------- monitor ----------------------

	private Thread monitorThread;
	private volatile boolean toStop = false;
	public void start(){
		AlarmFactory.creatAlarms();

		monitorThread = new Thread(new Runnable() {

			@Override
			public void run() {

				// monitor
				while (!toStop) {
					try {

						List<Long> failLogIds = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().findFailJobLogIds(1000);
						if (failLogIds!=null && !failLogIds.isEmpty()) {
							for (long failLogId: failLogIds) {

								// lock log
								int lockRet = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateAlarmStatus(failLogId, 0, -1, -1, -1);
								if (lockRet < 1) {
									continue;
								}
								XxlJobLog log = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().load(failLogId);
								XxlJobInfo info = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(log.getJobId());

								// 1、fail retry monitor
								if (log.getExecutorFailRetryCount() > 0) {
									JobTriggerPoolHelper.trigger(log.getJobId(), TriggerTypeEnum.RETRY, (log.getExecutorFailRetryCount()-1), log.getExecutorShardingParam(), null);
									String retryMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_type_retry") +"<<<<<<<<<<< </span><br>";
									log.setTriggerMsg(log.getTriggerMsg() + retryMsg);
									XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(log);
								}

								// 2、fail alarm monitor
								Integer[] alarmStatus = failAlarm(info, log);

								XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateAlarmStatus(failLogId, -1, alarmStatus[0], alarmStatus[1], alarmStatus[2]);
							}
						}

						TimeUnit.SECONDS.sleep(10);
					} catch (Exception e) {
						if (!toStop) {
							logger.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
						}
					}
				}

				logger.info(">>>>>>>>>>> xxl-job, job fail monitor thread stop");

			}
		});
		monitorThread.setDaemon(true);
		monitorThread.setName("xxl-job, admin JobFailMonitorHelper");
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


	// ---------------------- alarm ----------------------

	/**
	 * fail alarm
	 *
	 * @param jobLog
	 */
	private Integer[] failAlarm(XxlJobInfo info, XxlJobLog jobLog){
		Integer[] alarmResult = new Integer[]{1, 1, 1};// 告警状态：0-默认、-1=锁定状态、1-无需告警、2-告警成功、3-告警失败
		if (info!=null) {
			if (info.getAlarmEmail()!=null && info.getAlarmEmail().trim().length()>0) {
				IAlarm alarm = AlarmFactory.getAlarm(AlarmWay.EMAIL);
				if (null != alarm) {
					alarmResult[0] = alarm.failAlarm(info, jobLog)?2:3;
				}
			}
			if (info.getAlarmPhone()!=null && info.getAlarmPhone().trim().length()>0) {
				IAlarm alarm = AlarmFactory.getAlarm(AlarmWay.SMS);
				if (null != alarm) {
					alarmResult[1] = alarm.failAlarm(info, jobLog)?2:3;
				}
			}
			if (info.getAlarmDingRobot()!=null && info.getAlarmDingRobot().trim().length()>0) {
				IAlarm alarm = AlarmFactory.getAlarm(AlarmWay.DINGDING);
				if (null != alarm) {
					alarmResult[2] = alarm.failAlarm(info, jobLog)?2:3;
				}
			}
		}

		return alarmResult;
	}

}
