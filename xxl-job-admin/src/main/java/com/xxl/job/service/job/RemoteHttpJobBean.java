package com.xxl.job.service.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.xxl.job.client.handler.HandlerRepository;
import com.xxl.job.client.util.HttpUtil;
import com.xxl.job.client.util.HttpUtil.RemoteCallBack;
import com.xxl.job.client.util.JacksonUtil;
import com.xxl.job.core.model.XxlJobInfo;
import com.xxl.job.core.model.XxlJobLog;
import com.xxl.job.core.thread.JobMonitorHelper;
import com.xxl.job.core.util.DynamicSchedulerUtil;
import com.xxl.job.core.util.PropertiesUtil;

/**
 * http job bean
 * “@DisallowConcurrentExecution” diable concurrent, thread size can not be only one, better given more
 * @author xuxueli 2015-12-17 18:20:34
 */
@DisallowConcurrentExecution
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
		params.put(HandlerRepository.NAMESPACE, HandlerRepository.NameSpaceEnum.RUN.name());
		params.put(HandlerRepository.TRIGGER_LOG_URL, PropertiesUtil.getString(HandlerRepository.TRIGGER_LOG_URL));
		params.put(HandlerRepository.TRIGGER_LOG_ID, String.valueOf(jobLog.getId()));
		params.put(HandlerRepository.TRIGGER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
		params.put(HandlerRepository.HANDLER_NAME, jobDataMap.get(HandlerRepository.HANDLER_NAME));
		params.put(HandlerRepository.HANDLER_PARAMS, jobDataMap.get(HandlerRepository.HANDLER_PARAMS));

		// handler address, jetty or servlet
		String handler_address = jobDataMap.get(HandlerRepository.HANDLER_ADDRESS);
		if (!handler_address.startsWith("http")){
			handler_address = "http://" + handler_address + "/";
		}

		RemoteCallBack callback = HttpUtil.post(handler_address, params);
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