package com.dianping.job.service.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.dianping.job.client.handler.HandlerRepository;
import com.dianping.job.client.handler.IJobHandler.JobTriggerStatus;
import com.dianping.job.core.model.DianpingJobLog;
import com.dianping.job.core.util.DynamicSchedulerUtil;

/**
 * http job bean
 * @author xuxueli 2015-12-17 18:20:34
 */
public class HttpJobBean extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(HttpJobBean.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		
		String triggerKey = context.getTrigger().getKey().getName();
		String triggerGroup = context.getTrigger().getKey().getGroup();
		Map<String, Object> jobDataMap = context.getMergedJobDataMap().getWrappedMap();
		
		// jobDataMap 2 params
		Map<String, String> params = new HashMap<String, String>();
		if (jobDataMap!=null && jobDataMap.size()>0) {
			for (Entry<String, Object> item : jobDataMap.entrySet()) {
				params.put(item.getKey(), String.valueOf(item.getValue()));
			}
		}
		
		String job_url = params.get(DynamicSchedulerUtil.job_url);
		triggerPost(job_url, params);
		
		logger.info(">>>>>>>>>>> dianping-clock run :jobId:{}, group:{}, jobDataMap:{}", 
				new Object[]{triggerKey, triggerGroup, jobDataMap});
    }
	
	public static void triggerPost(String reqURL, Map<String, String> params){
		// save log
		DianpingJobLog jobLog = new DianpingJobLog();
		jobLog.setJobTriggerUuid(UUID.randomUUID().toString());
		jobLog.setJobHandleName(params.get(HandlerRepository.handleName));
		jobLog.setTriggerTime(new Date());
		logger.info(">>>>>>>>>>> dianping-clock trigger start :jobLog:{}", jobLog);
		
		// post
		String responseContent = null;
		HttpPost httpPost = new HttpPost(reqURL);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try{
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for(Map.Entry<String,String> entry : params.entrySet()){
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
			httpPost.setConfig(requestConfig);
			
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);
			}
			logger.info(">>>>>>>>>>> dianping-clock trigger ing :jobLog:{}, response:{}, responseContent:{}", jobLog, response, responseContent);
		} catch (Exception e) {
			e.printStackTrace();
			
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			responseContent = out.toString();
		} finally{
			httpPost.releaseConnection();
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// update trigger status
			if (responseContent!=null && responseContent.equals(JobTriggerStatus.SUCCESS.name())) {
				jobLog.setTriggerStatus(JobTriggerStatus.SUCCESS.name());
			} else {
				jobLog.setTriggerStatus(JobTriggerStatus.FAIL.name());
			}
			jobLog.setTriggerDetailLog(responseContent);
			if (jobLog.getTriggerDetailLog()!=null && jobLog.getTriggerDetailLog().length()>1000) {
				jobLog.setTriggerDetailLog(jobLog.getTriggerDetailLog().substring(0, 1000));
			}
			
			logger.info(">>>>>>>>>>> dianping-clock trigger end :jobLog:{}", jobLog);
		}
		
	}
	
	public static void main(String[] args) {
		String url = "http://localhost:8080/dianping-job-client-demo/dianpingJobServlet";
		
		for (int i = 0; i < 3; i++) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(HandlerRepository.handleName, "com.dianping.job.service.handler.DemoJobHandler");
			params.put(HandlerRepository.triggerUuid, i+"");
			params.put("key", i+"");
			
			triggerPost(url, params);
		}
	}
}