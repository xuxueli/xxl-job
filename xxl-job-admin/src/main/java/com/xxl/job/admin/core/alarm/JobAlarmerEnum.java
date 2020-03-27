package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.alarm.impl.EmailJobAlarm;
import com.xxl.job.admin.core.util.I18nUtil;

/**
 * @author Locki 2020-03-26
 */
public enum JobAlarmerEnum {
	EMAIL(I18nUtil.getString("alarminfo_email"), new EmailJobAlarm());

	JobAlarmerEnum(String title, JobAlarm alarm) {
		this.title = title;
		this.alarm = alarm;
	}

	private String title;
	private JobAlarm alarm;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public JobAlarm getAlarm() {
		return alarm;
	}

	public void setAlarm(JobAlarm alarm) {
		this.alarm = alarm;
	}

	public static JobAlarmerEnum match(String name, JobAlarmerEnum defaultItem) {
		if (name != null) {
			for (JobAlarmerEnum item : JobAlarmerEnum.values()) {
				if (item.name().equals(name)) {
					return item;
				}
			}
		}
		return defaultItem;
	}
}
