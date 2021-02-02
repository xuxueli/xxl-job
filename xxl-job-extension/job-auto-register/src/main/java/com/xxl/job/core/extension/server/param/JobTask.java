package com.xxl.job.core.extension.server.param;

import lombok.Data;

/**
 * @author lesl
 */
@Data
public class JobTask {

	/**
	 * cron 表达式
	 */
	private String cron;

	/**
	 * JobHandler
	 */
	private String jobHandler;
}
