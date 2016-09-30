package com.xxl.job.admin.core.jobbean;

import com.xxl.job.admin.core.callback.XxlJobLogCallbackServer;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.thread.JobMonitorHelper;
import com.xxl.job.admin.core.util.DynamicSchedulerUtil;
import com.xxl.job.core.registry.RegistHelper;
import com.xxl.job.core.router.HandlerRouter.ActionRepository;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;
import com.xxl.job.core.util.XxlJobNetCommUtil;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.MessageFormat;
import java.util.*;

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
		DynamicSchedulerUtil.xxlJobLogDao.save(jobLog);
		logger.info(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());
		
		// trigger request
		RequestModel requestModel = new RequestModel();
		requestModel.setTimestamp(System.currentTimeMillis());
		requestModel.setAction(ActionRepository.RUN.name());
		requestModel.setJobGroup(jobInfo.getJobGroup());
		requestModel.setJobName(jobInfo.getJobName());
		requestModel.setExecutorHandler(jobInfo.getExecutorHandler());
		requestModel.setExecutorParams(jobInfo.getExecutorParam());
		requestModel.setGlueSwitch((jobInfo.getGlueSwitch()==0)?false:true);
		requestModel.setLogAddress(XxlJobLogCallbackServer.getTrigger_log_address());
		requestModel.setLogId(jobLog.getId());

		// parse address
		List<String> addressList = new ArrayList<String>();
		String parseAddressMsg = null;
		if (StringUtils.isNotBlank(jobInfo.getExecutorAppname())) {
			List<XxlJobRegistry> xxlJobRegistryList = DynamicSchedulerUtil.xxlJobRegistryDao.findRegistrys(RegistHelper.RegistType.EXECUTOR.name(), jobInfo.getExecutorAppname());
			if (xxlJobRegistryList!=null && xxlJobRegistryList.size()>0) {
				for (XxlJobRegistry item: xxlJobRegistryList) {
					addressList.add(item.getRegistryValue());
				}
			}
			parseAddressMsg = MessageFormat.format("Parse Address (Appname注册方式) <br>>>>[address list] : {0}<br><hr>", addressList.toArray());
		} else {
			List<String> addressArr = Arrays.asList(jobInfo.getExecutorAddress().split(","));
			addressList.addAll(addressArr);
			parseAddressMsg = MessageFormat.format("Parse Address (地址配置方式) <br>>>>[address list] : {0}<br><hr>", addressList.toArray());
		}

		// failover trigger
		ResponseModel responseModel = failoverTrigger(addressList, requestModel, jobLog);
		jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
		jobLog.setExecutorParam(jobInfo.getExecutorParam());
		logger.info(">>>>>>>>>>> xxl-job failoverTrigger response, jobId:{}, responseModel:{}", jobLog.getId(), responseModel.toString());
		
		// update trigger info
		jobLog.setTriggerTime(new Date());
		jobLog.setTriggerStatus(responseModel.getStatus());
		jobLog.setTriggerMsg(parseAddressMsg + responseModel.getMsg());
		DynamicSchedulerUtil.xxlJobLogDao.updateTriggerInfo(jobLog);

		// monitor triger
		JobMonitorHelper.monitor(jobLog.getId());
		
		logger.info(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }
	
	
	/**
	 * failover for trigger remote address
	 * @return
	 */
	public ResponseModel failoverTrigger(List<String> addressList, RequestModel requestModel, XxlJobLog jobLog){
		if (addressList.size() > 1) {
			
			// for ha
			Collections.shuffle(addressList);

			// for failover
			String failoverMessage = "";
			for (String address : addressList) {
				if (StringUtils.isNotBlank(address)) {

					// beat check
					RequestModel beatRequest = new RequestModel();
					beatRequest.setTimestamp(System.currentTimeMillis());
					beatRequest.setAction(ActionRepository.BEAT.name());
					ResponseModel beatResult = XxlJobNetCommUtil.postHex(XxlJobNetCommUtil.addressToUrl(address), beatRequest);
					failoverMessage += MessageFormat.format("BEAT running, <br>>>>[address] : {0}, <br>>>>[status] : {1}, <br>>>>[msg] : {2} <br><hr>", address, beatResult.getStatus(), beatResult.getMsg());

					// beat success, trigger do
					if (beatResult.SUCCESS.equals(beatResult.getStatus())) {
						// store real address
						jobLog.setExecutorAddress(address);

						// real trigger
						ResponseModel triggerCallback = XxlJobNetCommUtil.postHex(XxlJobNetCommUtil.addressToUrl(address), requestModel);
						failoverMessage += MessageFormat.format("Trigger running, <br>>>>[address] : {0}, <br>>>>[status] : {1}, <br>>>>[msg] : {2} <br><hr>", address, triggerCallback.getStatus(), triggerCallback.getMsg());
						triggerCallback.setMsg(failoverMessage);
						return triggerCallback;
					}

				}
			}

			ResponseModel result = new ResponseModel();
			result.setStatus(ResponseModel.FAIL);
			result.setMsg(failoverMessage);
			return result;
		} else if (addressList.size() == 1) {
			String address = addressList.get(0);
			// store real address
			jobLog.setExecutorAddress(address);

			ResponseModel triggerCallback = XxlJobNetCommUtil.postHex(XxlJobNetCommUtil.addressToUrl(address), requestModel);
			String failoverMessage = MessageFormat.format("Trigger running, <br>>>>[address] : {0}, <br>>>>[status] : {1}, <br>>>>[msg] : {2} <br><hr>", address, triggerCallback.getStatus(), triggerCallback.getMsg());
			triggerCallback.setMsg(failoverMessage);
			return triggerCallback;
		} else {
			ResponseModel result = new ResponseModel();
			result.setStatus(ResponseModel.FAIL);
			result.setMsg( "Trigger error, <br>>>>address list is null <br><hr>" );
			return result;
		}
	}

	
}