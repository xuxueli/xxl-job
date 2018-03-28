package com.xxl.job.executor.service.jobhandler.other;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.candao.irms.framework.net.http.HttpClient;
import com.candao.irms.framework.net.http.HttpResult;
import com.candao.irms.framework.util.StringUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.bean.ResponseBean;
import com.xxl.job.executor.core.config.URLConfig;
import com.xxl.job.executor.util.Sign;

/**
 * 所有服务的定时任务汇集
 * 
 * @author jeromeLiu
 *
 */
@JobHandler(value = "allJobHandler")
@Component
public class AllJobHandler extends IJobHandler{

	/* 
	 * executeParam:
	 * 				{
	 *					"taskUrl":"http://10.200.102.130/xxxx/xxxxx",
	 *                  "jobName":"GenerateFinishOrderReportJobHandler",
	 *                  "taskId": 43,
	 *                  "callBack": "http://58.248.185.158:8010/jobinfo/callback/43/",
	 *					"bodyParam":{
	 *						"taskType": "updateStoreRuleData"
	 *						"extParam": {
	 *						
	 *						}
	 *					}
	 *			 	}
	 * 
	 * (non-Javadoc)
	 * @see com.xxl.job.core.handler.IJobHandler#execute(java.lang.String)
	 */
	@Override
	public ReturnT<String> execute(String executeParam) throws Exception {
		JSONObject json = JSONObject.parseObject(executeParam);
		
		String jobName = json.getString("jobName");
		String taskUrl = json.getString("taskUrl");
		String taskId = json.getString("taskId");
		String callBack = json.getString("callBack");
		
		JSONObject newJson = JSONObject.parseObject(json.get("bodyParam").toString());
		newJson.put("taskId", taskId);
		newJson.put("callBackUrl", callBack);
		
		String bodyParam = newJson.toJSONString();
		
		if (StringUtil.isNullOrEmpty(taskUrl)) {
			ReturnT<String> returnT = new ReturnT<String>(ReturnT.FAIL_CODE,"调度url不能为空");
			return returnT;
		}
		
		if (StringUtil.isNullOrEmpty(bodyParam)) {
			ReturnT<String> returnT = new ReturnT<String>(ReturnT.FAIL_CODE,"bodyParam不能为空");
			return returnT;
		}
		
		XxlJobLogger.log(jobName + "-JOB, 启动");

		XxlJobLogger.log("===================定时任务【"+jobName+"】开始======================");
		Long pushTime = System.currentTimeMillis();

		String url = taskUrl + "?pushTime=" + pushTime;

		String signParam = Sign.get32MD5(pushTime.toString());

		String[][] headers = new String[][] { { "jobReqValidationKey", signParam },{ "Content-type", "application/json" } };

		HttpResult result = HttpClient.postWithBody(url, bodyParam, 10, headers);
		
		ReturnT<String> returnT = null;
		
		if (result.statusCode == 200) {
			ResponseBean bean = JSONObject.parseObject(result.content, ResponseBean.class);
			returnT = new ReturnT<String>(result.statusCode, result.content);
			if (bean.getStatus() != 1) {
				returnT = new ReturnT<String>(ReturnT.FAIL_CODE, result.content);
			}
		}else{
			returnT = new ReturnT<String>(result.statusCode, result.content);
		}
		
		XxlJobLogger.log("时间戳:pushTime = " + pushTime);
		XxlJobLogger.log("封签密钥:sign = " + URLConfig.SIGN_KEY);
		XxlJobLogger.log("请求头:jobReqValidationKey = " + signParam);
		XxlJobLogger.log("请求报文:body = " + bodyParam);
		XxlJobLogger.log("请求url:url = " + url);
		XxlJobLogger.log("请求结果:result = " + JSONObject.toJSONString(result));

		XxlJobLogger.log("===================定时任务【"+jobName+"】结束======================");

		return returnT;
	}

}
