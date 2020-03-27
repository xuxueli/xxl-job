package com.xxl.job.admin.core.alarm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlAlarmInfo;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;

@Component
public class JobAlarmer {
    private static Logger logger = LoggerFactory.getLogger(JobAlarmer.class);

	/**
	 * job alarm
	 *
	 * @param info
	 * @param jobLog
	 * @return
	 */
	public boolean alarm(XxlJobInfo info, XxlJobLog jobLog) {
		boolean result = true;
		//fetch alarmers
		List<XxlAlarmInfo> jobAlarmList = null;
		if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {
			String[] idStrs = info.getAlarmEmail().split(",");
			long[] ids = new long[idStrs.length];
			for (int i = 0; i < idStrs.length; i++) {
				try {
					ids[i] = Long.valueOf(idStrs[i]);
				} catch (Exception e) {
					logger.error("alarm id error [{}]", idStrs[i]);
					logger.error(e.getMessage(), e);
				}
			}
			jobAlarmList = XxlJobAdminConfig.getAdminConfig().getAlarmInfoDao().listInfo(ids);
		}
		
		// alarm
		if (jobAlarmList != null && jobAlarmList.size() > 0) {
			for (XxlAlarmInfo alarm : jobAlarmList) {
				JobAlarmerEnum alarmer = JobAlarmerEnum.match(alarm.getAlarmType(), null);
				if (alarmer != null) {
					try {
						result = alarmer.getAlarm().doAlarm(info, jobLog, alarm) && result;
					} catch (Exception e) {
						result = false;
						logger.error(e.getMessage(), e);
					}
				} else {
					result = false;
					logger.debug("alarmer [{}] dosen't exist", alarm.getAlarmType());
				}
			}
		}
		return result;
	}
}
