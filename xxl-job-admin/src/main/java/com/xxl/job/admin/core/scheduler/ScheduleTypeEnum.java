package com.xxl.job.admin.core.scheduler;

import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.util.XxlJobTool;

/**
 * @author xuxueli 2020-10-29 21:11:23
 */
public enum ScheduleTypeEnum {

	NONE(I18nUtil.getString("schedule_type_none")),

	/**
	 * schedule by cron
	 */
	CRON(I18nUtil.getString("schedule_type_cron")),

	/**
	 * schedule by fixed rate (in seconds)
	 */
	FIX_RATE(I18nUtil.getString("schedule_type_fix_rate")),

	/**
	 * schedule by fix delay (in seconds)， after the last time
	 */
	/*FIX_DELAY(I18nUtil.getString("schedule_type_fix_delay"))*/;

	final String title;

	ScheduleTypeEnum(String title) {
		this.title = title;
	}

	public static ScheduleTypeEnum match(String name, ScheduleTypeEnum defaultItem) {
		return XxlJobTool.getEnum(ScheduleTypeEnum.class, name, defaultItem);
	}

	public String getTitle() {
		return title;
	}

}