package com.xxl.job.admin.core.jobbean;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.core.thread.JobMonitorHelper;
import com.xxl.job.admin.core.thread.JobRegistryHelper;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.registry.RegistHelper;
import com.xxl.job.core.rpc.netcom.NetComClientProxy;
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
		Integer jobId = Integer.valueOf(jobKey.getName());
		XxlJobInfo jobInfo = XxlJobDynamicScheduler.xxlJobInfoDao.loadById(jobId);

		// save log
		XxlJobLog jobLog = new XxlJobLog();
		jobLog.setJobGroup(jobInfo.getJobGroup());
		jobLog.setJobId(jobInfo.getId());
		XxlJobDynamicScheduler.xxlJobLogDao.save(jobLog);
		logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // admin address
        List<String> adminAddressList = JobRegistryHelper.discover(RegistHelper.RegistType.ADMIN.name(), RegistHelper.RegistType.ADMIN.name());
		Set<String> adminAddressSet = new HashSet<String>();
        if (adminAddressList!=null) {
            adminAddressSet.addAll(adminAddressList);
        }
        adminAddressSet.add(XxlJobDynamicScheduler.getCallbackAddress());

		// update trigger info 1/2
		jobLog.setTriggerTime(new Date());

		// trigger request
		TriggerParam triggerParam = new TriggerParam();
		triggerParam.setJobId(jobInfo.getId());
		triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
		triggerParam.setExecutorParams(jobInfo.getExecutorParam());
		triggerParam.setGlueSwitch((jobInfo.getGlueSwitch()==0)?false:true);
		triggerParam.setLogId(jobLog.getId());
		triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
		triggerParam.setLogAddress(adminAddressSet);

		// parse address
		String groupAddressInfo = "注册方式：";
		List<String> addressList = new ArrayList<String>();
		XxlJobGroup group = XxlJobDynamicScheduler.xxlJobGroupDao.load(Integer.valueOf(jobInfo.getJobGroup()));
		if (group!=null) {
			if (group.getAddressType() == 0) {
				groupAddressInfo += "自动注册";
				addressList = JobRegistryHelper.discover(RegistHelper.RegistType.EXECUTOR.name(), group.getAppName());
			} else {
				groupAddressInfo += "手动录入";
				if (StringUtils.isNotBlank(group.getAddressList())) {
					addressList = Arrays.asList(group.getAddressList().split(","));
				}
			}
			groupAddressInfo += "，地址列表：" + addressList.toString();
		}
        groupAddressInfo += "<br><br>";

		// failover trigger
		ReturnT<String> triggerResult = failoverTrigger(addressList, triggerParam, jobLog);
		jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
		jobLog.setExecutorParam(jobInfo.getExecutorParam());
		logger.info(">>>>>>>>>>> xxl-job failoverTrigger, jobId:{}, triggerResult:{}", jobLog.getId(), triggerResult.toString());
		
		// update trigger info 2/2
		jobLog.setTriggerCode(triggerResult.getCode());
		jobLog.setTriggerMsg(groupAddressInfo + triggerResult.getMsg());
		XxlJobDynamicScheduler.xxlJobLogDao.updateTriggerInfo(jobLog);

		// monitor triger
		JobMonitorHelper.monitor(jobLog.getId());
		
		logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }
	
	
	/**
	 * failover for trigger remote address
	 * @return
	 */
	public ReturnT<String> failoverTrigger(List<String> addressList, TriggerParam triggerParam, XxlJobLog jobLog){
		 if (addressList==null || addressList.size() < 1) {
			 return new ReturnT<String>(ReturnT.FAIL_CODE, "Trigger error, <br>>>>[address] is null <br><hr>");
		} else if (addressList.size() == 1) {
			 String address = addressList.get(0);
			 // store real address
			 jobLog.setExecutorAddress(address);

			 // real trigger
			 ExecutorBiz executorBiz = null;
			 try {
				 executorBiz = (ExecutorBiz) new NetComClientProxy(ExecutorBiz.class, address).getObject();
			 } catch (Exception e) {
				 e.printStackTrace();
				 return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
			 }
			 ReturnT<String> runResult = executorBiz.run(triggerParam);

			 String failoverMessage = MessageFormat.format("Trigger running, <br>>>>[address] : {0}, <br>>>>[code] : {1}, <br>>>>[msg] : {2} <br><hr>",
					 address, runResult.getCode(), runResult.getMsg());
			 runResult.setMsg(runResult.getMsg() + failoverMessage);
			 return runResult;
		 } else {
			
			// for ha
			Collections.shuffle(addressList);

			// for failover
			String failoverMessage = "";
			for (String address : addressList) {
				if (StringUtils.isNotBlank(address)) {


                    ExecutorBiz executorBiz = null;
                    try {
                        executorBiz = (ExecutorBiz) new NetComClientProxy(ExecutorBiz.class, address).getObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
                    }

                    // beat check
					ReturnT<String> beatResult = executorBiz.beat();
					failoverMessage += MessageFormat.format("BEAT running, <br>>>>[address] : {0}, <br>>>>[code] : {1}, <br>>>>[msg] : {2} <br><hr>",
							address, beatResult.getCode(), beatResult.getMsg());

					// beat success, trigger do
					if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {
						// store real address
						jobLog.setExecutorAddress(address);

						// real trigger
						ReturnT<String> runResult = executorBiz.run(triggerParam);

						failoverMessage += MessageFormat.format("Trigger running, <br>>>>[address] : {0}, <br>>>>[status] : {1}, <br>>>>[msg] : {2} <br><hr>",
								address, runResult.getCode(), runResult.getMsg());
						runResult.setMsg( runResult.getMsg() + failoverMessage);
						return runResult;
					}

				}
			}

			return new ReturnT<String>(ReturnT.FAIL_CODE, failoverMessage);
		}
	}

	
}