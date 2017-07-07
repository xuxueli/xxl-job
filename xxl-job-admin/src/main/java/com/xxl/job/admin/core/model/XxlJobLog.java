package com.xxl.job.admin.core.model;

import lombok.Data;

import java.util.Date;

/**
 * xxl-job log, used to track trigger process
 * @author xuxueli  2015-12-19 23:19:09
 */
@Data
public class XxlJobLog {
	
	private int id;
	
	// job info
	private int jobGroup;
	private int jobId;

	// glueType
	private String glueType;

	// execute info
	private String executorAddress;
	private String executorHandler;
	private String executorParam;
	
	// trigger info
	private Date triggerTime;
	private int triggerCode;
	private String triggerMsg;
	
	// handle info
	private Date handleTime;
	private int handleCode;
	private String handleMsg;
}
