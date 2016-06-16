package com.xxl.job.admin.core.jobbean;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.xxl.job.core.handler.HandlerRepository.ActionEnum;
import com.xxl.job.core.handler.HandlerRepository.HandlerParamEnum;
import com.xxl.job.core.util.HttpUtil;
import com.xxl.job.core.util.HttpUtil.RemoteCallBack;

/**
 * http job bean
 * “@DisallowConcurrentExecution” diable concurrent, thread size can not be only one, better given more
 * @author xuxueli 2015-12-17 18:20:34
 */
//@DisallowConcurrentExecution
public class RemoteHttpJobBean extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(RemoteHttpJobBean.class);
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		JobKey jobKey = context.getTrigger().getJobKey();
		
		XxlJobInfo jobInfo = DynamicSchedulerUtil.xxlJobInfoDao.load(jobKey.getGroup(), jobKey.getName());
		// save log
		XxlJobLog jobLog = new XxlJobLog();
		jobLog.setJobGroup(jobInfo.getJobGroup());
		jobLog.setJobName(jobInfo.getJobName());
		jobLog.setJobCron(jobInfo.getJobCron());
		jobLog.setJobDesc(jobInfo.getJobDesc());
		jobLog.setJobClass(jobInfo.getJobClass());
		DynamicSchedulerUtil.xxlJobLogDao.save(jobLog);
		logger.info(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());
		
		// trigger request
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(HandlerParamEnum.TIMESTAMP.name(), String.valueOf(System.currentTimeMillis()));
		params.put(HandlerParamEnum.ACTION.name(), ActionEnum.RUN.name());
		
		params.put(HandlerParamEnum.LOG_ADDRESS.name(), XxlJobLogCallbackServer.getTrigger_log_address());
		params.put(HandlerParamEnum.LOG_ID.name(), String.valueOf(jobLog.getId()));
		
		params.put(HandlerParamEnum.EXECUTOR_HANDLER.name(), jobInfo.getExecutorHandler());
		params.put(HandlerParamEnum.EXECUTOR_PARAMS.name(), jobInfo.getExecutorParam());
		
		params.put(HandlerParamEnum.GLUE_SWITCH.name(), String.valueOf(jobInfo.getGlueSwitch()));
		params.put(HandlerParamEnum.JOB_GROUP.name(), jobInfo.getJobGroup());
		params.put(HandlerParamEnum.JOB_NAME.name(), jobInfo.getJobName());

		// failover trigger
		RemoteCallBack callback = failoverTrigger(jobInfo.getExecutorAddress(), params, jobLog);
		jobLog.setExecutorHandler(jobInfo.getGlueSwitch()==0?jobInfo.getExecutorHandler():"GLUE任务");
		jobLog.setExecutorParam(jobInfo.getExecutorParam());
		logger.info(">>>>>>>>>>> xxl-job failoverTrigger response, jobId:{}, callback:{}", jobLog.getId(), callback);
		
		// update trigger info
		jobLog.setTriggerTime(new Date());
		jobLog.setTriggerStatus(callback.getStatus());
		jobLog.setTriggerMsg(callback.getMsg());
		DynamicSchedulerUtil.xxlJobLogDao.updateTriggerInfo(jobLog);

		// monitor triger
		JobMonitorHelper.monitor(jobLog.getId());
		
		logger.info(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }
	
	
	/**
	 * failover for trigger remote address
	 * @param addressArr
	 * @return
	 */
	public RemoteCallBack failoverTrigger(String handler_address, HashMap<String, String> handler_params, XxlJobLog jobLog){
		if (handler_address.split(",").length > 1) {
			
			// for ha
			List<String> addressList = Arrays.asList(handler_address.split(","));
			Collections.shuffle(addressList);
			
			// for failover
			String failoverMessage = "";
			for (String address : addressList) {
				if (StringUtils.isNotBlank(address)) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put(HandlerParamEnum.TIMESTAMP.name(), String.valueOf(System.currentTimeMillis()));
					params.put(HandlerParamEnum.ACTION.name(), ActionEnum.BEAT.name());
					RemoteCallBack beatResult = HttpUtil.post(HttpUtil.addressToUrl(address), params);
					failoverMessage += MessageFormat.format("BEAT running, <br>>>>[address] : {0}, <br>>>>[status] : {1}, <br>>>>[msg] : {2} <br><hr>", address, beatResult.getStatus(), beatResult.getMsg());
					if (RemoteCallBack.SUCCESS.equals(beatResult.getStatus())) {
						jobLog.setExecutorAddress(address);
						RemoteCallBack triggerCallback = HttpUtil.post(HttpUtil.addressToUrl(address), handler_params);
						triggerCallback.setStatus(RemoteCallBack.SUCCESS);
						failoverMessage += MessageFormat.format("Trigger running, <br>>>>[address] : {0}, <br>>>>[status] : {1}, <br>>>>[msg] : {2} <br><hr>", address, triggerCallback.getStatus(), triggerCallback.getMsg());
						triggerCallback.setMsg(failoverMessage);
						return triggerCallback;
					}
				}
			}
			
			RemoteCallBack result = new RemoteCallBack();
			result.setStatus(RemoteCallBack.FAIL);
			result.setMsg(failoverMessage);
			return result;
		} else {
			jobLog.setExecutorAddress(handler_address);
			RemoteCallBack triggerCallback = HttpUtil.post(HttpUtil.addressToUrl(handler_address), handler_params);
			String failoverMessage = MessageFormat.format("Trigger running, <br>>>>[address] : {0}, <br>>>>[status] : {1}, <br>>>>[msg] : {2} <br><hr>", handler_address, triggerCallback.getStatus(), triggerCallback.getMsg());
			triggerCallback.setMsg(failoverMessage);
			return triggerCallback;
		}
	}
	
}