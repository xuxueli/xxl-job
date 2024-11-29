package com.xxl.job.admin.core.scheduler;

import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.util.XxlJobTool;

/**
 * @author xuxueli 2020-10-29 21:11:23
 */
public enum MisfireStrategyEnum {

	/**
	 * do nothing
	 */
	DO_NOTHING(I18nUtil.getString("misfire_strategy_do_nothing")),

	/**
	 * fire once now
	 */
	FIRE_ONCE_NOW(I18nUtil.getString("misfire_strategy_fire_once_now"));

	final String title;

	MisfireStrategyEnum(String title) {
		this.title = title;
	}

	public static MisfireStrategyEnum match(String name, MisfireStrategyEnum defaultItem) {
		return XxlJobTool.getEnum(MisfireStrategyEnum.class, name, defaultItem);
	}

	public String getTitle() {
		return title;
	}

}