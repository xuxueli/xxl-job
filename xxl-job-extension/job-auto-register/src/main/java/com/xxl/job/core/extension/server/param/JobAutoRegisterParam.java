package com.xxl.job.core.extension.server.param;

import java.util.List;
import lombok.Data;

/**
 * @author lesl
 */
@Data
public class JobAutoRegisterParam {

	/**
	 * app名称
	 */
	private String appName;

	/**
	 * app 标题
	 */
	private String appTitle;
	/**
	 * 任务列表
	 */
	private List<JobTask> jobTasks;
}
