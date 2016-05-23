package com.xxl.job.admin.core.jobbean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.xxl.job.admin.core.callback.XxlJobLogCallbackServer;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.thread.JobMonitorHelper;
import com.xxl.job.admin.core.util.DynamicSchedulerUtil;
import com.xxl.job.core.handler.HandlerRepository;
import com.xxl.job.core.util.HttpUtil;
import com.xxl.job.core.util.HttpUtil.RemoteCallBack;
import com.xxl.job.core.util.JacksonUtil;

/**
 * http job bean
 * “@DisallowConcurrentExecution” diable concurrent, thread size can not be only one, better given more
 * @author xuxueli 2015-12-17 18:20:34
 */
//@DisallowConcurrentExecution
public class RemoteHttpJobBean extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(RemoteHttpJobBean.class);
	
	@SuppressWarnings("unchecked")
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		JobKey jobKey = context.getTrigger().getJobKey();
		
		XxlJobInfo jobInfo = DynamicSchedulerUtil.xxlJobInfoDao.load(jobKey.getGroup(), jobKey.getName());
		HashMap<String, String> jobDataMap = (HashMap<String, String>) JacksonUtil.readValueRefer(jobInfo.getJobData(), Map.class);
		// save log
		XxlJobLog jobLog = new XxlJobLog();
		jobLog.setJobGroup(jobInfo.getJobGroup());
		jobLog.setJobName(jobInfo.getJobName());
		jobLog.setJobCron(jobInfo.getJobCron());
		jobLog.setJobDesc(jobInfo.getJobDesc());
		jobLog.setJobClass(jobInfo.getJobClass());
		jobLog.setJobData(jobInfo.getJobData());
		
		jobLog.setJobClass(RemoteHttpJobBean.class.getName());
		jobLog.setJobData(jobInfo.getJobData());
		DynamicSchedulerUtil.xxlJobLogDao.save(jobLog);
		logger.info(">>>>>>>>>>> xxl-job trigger start, jobLog:{}", jobLog);
		
		// trigger request
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(HandlerRepository.TRIGGER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
		params.put(HandlerRepository.NAMESPACE, HandlerRepository.NameSpaceEnum.RUN.name());
		
		params.put(HandlerRepository.TRIGGER_LOG_ID, String.valueOf(jobLog.getId()));
		params.put(HandlerRepository.TRIGGER_LOG_ADDRESS, XxlJobLogCallbackServer.getTrigger_log_address());
		
		params.put(HandlerRepository.HANDLER_NAME, jobDataMap.get(HandlerRepository.HANDLER_NAME));
		params.put(HandlerRepository.HANDLER_PARAMS, jobDataMap.get(HandlerRepository.HANDLER_PARAMS));
		
		params.put(HandlerRepository.HANDLER_GLUE_SWITCH, String.valueOf(jobInfo.getGlueSwitch()));
		params.put(HandlerRepository.HANDLER_JOB_GROUP, jobInfo.getJobGroup());
		params.put(HandlerRepository.HANDLER_JOB_NAME, jobInfo.getJobName());
		

		// handler address, jetty (servlet dead)
		String handler_address = jobDataMap.get(HandlerRepository.HANDLER_ADDRESS);

		RemoteCallBack callback = HttpUtil.post(HttpUtil.addressToUrl(handler_address), params);
		logger.info(">>>>>>>>>>> xxl-job trigger http response, jobLog.id:{}, jobLog:{}, callback:{}", jobLog.getId(), jobLog, callback);

		// update trigger info
		jobLog.setTriggerTime(new Date());
		jobLog.setTriggerStatus(callback.getStatus());
		jobLog.setTriggerMsg(callback.getMsg());
		DynamicSchedulerUtil.xxlJobLogDao.updateTriggerInfo(jobLog);

		// monitor triger
		JobMonitorHelper.monitor(jobLog.getId());
		
		logger.info(">>>>>>>>>>> xxl-job trigger end, jobLog.id:{}, jobLog:{}", jobLog.getId(), jobLog);
    }
	
}