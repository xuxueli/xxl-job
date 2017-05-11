package com.xxl.job.admin.core.jobbean;

import com.xxl.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.core.thread.JobFailMonitorHelper;
import com.xxl.job.admin.core.thread.JobRegistryMonitorHelper;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.rpc.netcom.NetComClientProxy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

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

		// load job
		JobKey jobKey = context.getTrigger().getJobKey();
		Integer jobId = Integer.valueOf(jobKey.getName());
		XxlJobInfo jobInfo = XxlJobDynamicScheduler.xxlJobInfoDao.loadById(jobId);

		// log part-1
		XxlJobLog jobLog = new XxlJobLog();
		jobLog.setJobGroup(jobInfo.getJobGroup());
		jobLog.setJobId(jobInfo.getId());
		XxlJobDynamicScheduler.xxlJobLogDao.save(jobLog);
		logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

		// log part-2 param
		//jobLog.setExecutorAddress(executorAddress);
		jobLog.setGlueType(jobInfo.getGlueType());
		jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
		jobLog.setExecutorParam(jobInfo.getExecutorParam());
		jobLog.setTriggerTime(new Date());

		// trigger request
		TriggerParam triggerParam = new TriggerParam();
		triggerParam.setJobId(jobInfo.getId());
		triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
		triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
		triggerParam.setGlueType(jobInfo.getGlueType());
		triggerParam.setGlueSource(jobInfo.getGlueSource());
		triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
		triggerParam.setLogId(jobLog.getId());
		triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());

		// do trigger
		ReturnT<String> triggerResult = doTrigger(triggerParam, jobInfo, jobLog);

		// fail retry
		if (triggerResult.getCode()==ReturnT.FAIL_CODE &&
				ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == ExecutorFailStrategyEnum.FAIL_RETRY) {
			ReturnT<String> retryTriggerResult = doTrigger(triggerParam, jobInfo, jobLog);

			triggerResult.setCode(retryTriggerResult.getCode());
			triggerResult.setMsg(triggerResult.getMsg() + "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>失败重试<<<<<<<<<<< </span><br><br>" +retryTriggerResult.getMsg());
		}

		// log part-2
		jobLog.setTriggerCode(triggerResult.getCode());
		jobLog.setTriggerMsg(triggerResult.getMsg());
		XxlJobDynamicScheduler.xxlJobLogDao.updateTriggerInfo(jobLog);

		// monitor triger
		JobFailMonitorHelper.monitor(jobLog.getId());
		logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }

    public ReturnT<String> doTrigger(TriggerParam triggerParam, XxlJobInfo jobInfo, XxlJobLog jobLog){
		StringBuffer triggerSb = new StringBuffer();

		// exerutor address list
		ArrayList<String> addressList = null;
		XxlJobGroup group = XxlJobDynamicScheduler.xxlJobGroupDao.load(jobInfo.getJobGroup());
		if (group.getAddressType() == 0) {
			triggerSb.append("注册方式：自动注册");
			addressList = (ArrayList<String>) JobRegistryMonitorHelper.discover(RegistryConfig.RegistType.EXECUTOR.name(), group.getAppName());
		} else {
			triggerSb.append("注册方式：手动录入");
			if (StringUtils.isNotBlank(group.getAddressList())) {
				addressList = new ArrayList<String>(Arrays.asList(group.getAddressList().split(",")));
			}
		}
		triggerSb.append("<br>阻塞处理策略：").append(ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION).getTitle());
        triggerSb.append("<br>失败处理策略：").append(ExecutorFailStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorFailStrategyEnum.FAIL_ALARM).getTitle());
		triggerSb.append("<br>地址列表：").append(addressList!=null?addressList.toString():"");
		if (CollectionUtils.isEmpty(addressList)) {
			triggerSb.append("<br>----------------------<br>").append("调度失败：").append("执行器地址为空");
			return new ReturnT<String>(ReturnT.FAIL_CODE, triggerSb.toString());
		}

		// trigger remote executor
		if (addressList.size() == 1) {
			String address = addressList.get(0);
			jobLog.setExecutorAddress(address);

			ReturnT<String> runResult = runExecutor(triggerParam, address);
			triggerSb.append("<br>----------------------<br>").append(runResult.getMsg());

			return new ReturnT<String>(runResult.getCode(), triggerSb.toString());
		} else {
			// executor route strategy
			ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
			triggerSb.append("<br>路由策略：").append(executorRouteStrategyEnum!=null?(executorRouteStrategyEnum.name() + "-" + executorRouteStrategyEnum.getTitle()):null);
			if (executorRouteStrategyEnum == null) {
				triggerSb.append("<br>----------------------<br>").append("调度失败：").append("执行器路由策略为空");
				return new ReturnT<String>(ReturnT.FAIL_CODE, triggerSb.toString());
			}

			if (executorRouteStrategyEnum != ExecutorRouteStrategyEnum.FAILOVER) {
				// get address
				String address = executorRouteStrategyEnum.getRouter().route(jobInfo.getId(), addressList);
				jobLog.setExecutorAddress(address);

				// run
				ReturnT<String> runResult = runExecutor(triggerParam, address);
				triggerSb.append("<br>----------------------<br>").append(runResult.getMsg());

				return new ReturnT<String>(runResult.getCode(), triggerSb.toString());
			} else {
				for (String address : addressList) {
					// beat
					ReturnT<String> beatResult = beatExecutor(address);
					triggerSb.append("<br>----------------------<br>").append(beatResult.getMsg());

					if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {
						jobLog.setExecutorAddress(address);

						ReturnT<String> runResult = runExecutor(triggerParam, address);
						triggerSb.append("<br>----------------------<br>").append(runResult.getMsg());

						return new ReturnT<String>(runResult.getCode(), triggerSb.toString());
					}
				}
				return new ReturnT<String>(ReturnT.FAIL_CODE, triggerSb.toString());
			}
		}
	}

	/**
	 * run executor
	 * @param address
	 * @return
	 */
	public ReturnT<String> beatExecutor(String address){
		ReturnT<String> beatResult = null;
		try {
			ExecutorBiz executorBiz = (ExecutorBiz) new NetComClientProxy(ExecutorBiz.class, address).getObject();
			beatResult = executorBiz.beat();
		} catch (Exception e) {
			logger.error("", e);
			beatResult = new ReturnT<String>(ReturnT.FAIL_CODE, ""+e );
		}

		StringBuffer sb = new StringBuffer("心跳检测：");
		sb.append("<br>address：").append(address);
		sb.append("<br>code：").append(beatResult.getCode());
		sb.append("<br>msg：").append(beatResult.getMsg());
		beatResult.setMsg(sb.toString());

		return beatResult;
	}

	/**
	 * run executor
	 * @param triggerParam
	 * @param address
	 * @return
	 */
	public ReturnT<String> runExecutor(TriggerParam triggerParam, String address){
		ReturnT<String> runResult = null;
		try {
			ExecutorBiz executorBiz = (ExecutorBiz) new NetComClientProxy(ExecutorBiz.class, address).getObject();
			runResult = executorBiz.run(triggerParam);
		} catch (Exception e) {
			logger.error("", e);
			runResult = new ReturnT<String>(ReturnT.FAIL_CODE, ""+e );
		}

		StringBuffer sb = new StringBuffer("触发调度：");
		sb.append("<br>address：").append(address);
		sb.append("<br>code：").append(runResult.getCode());
		sb.append("<br>msg：").append(runResult.getMsg());
		runResult.setMsg(sb.toString());

		return runResult;
	}

}