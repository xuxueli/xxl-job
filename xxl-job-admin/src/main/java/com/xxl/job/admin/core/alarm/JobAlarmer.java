package com.xxl.job.admin.core.alarm;

import java.util.List;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobAlarmer {

	private static final Logger logger = LoggerFactory.getLogger(JobAlarmer.class);

	private List<JobAlarm> jobAlarmList;

	@Autowired(required = false)
	public void setJobAlarmList(List<JobAlarm> jobAlarmList) {
		this.jobAlarmList = jobAlarmList;
	}

	/**
	 * job alarm
	 */
	public boolean alarm(XxlJobInfo info, XxlJobLog jobLog) {
		boolean result = false;
		final List<JobAlarm> list = jobAlarmList;
		if (list != null && !list.isEmpty()) {
			result = true;  // success means all-success
			for (JobAlarm alarm : list) {
				boolean resultItem = false;
				try {
					resultItem = alarm.doAlarm(info, jobLog);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				if (!resultItem) {
					result = false;
				}
			}
		}
		return result;
	}

}