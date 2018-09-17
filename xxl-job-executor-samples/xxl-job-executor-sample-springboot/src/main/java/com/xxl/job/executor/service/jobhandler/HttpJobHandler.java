package com.xxl.job.executor.service.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 跨平台Http任务
 *
 * @author xuxueli 2018-09-16 03:48:34
 */
@JobHandler(value="httpJobHandler")
@Component
public class HttpJobHandler extends IJobHandler {

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		// valid
		if (param==null || param.trim().length()==0) {
			XxlJobLogger.log("URL Empty");
			return FAIL;
		}

		// httpGet config
		HttpGet httpGet = new HttpGet(param);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
		httpGet.setConfig(requestConfig);

		CloseableHttpClient httpClient = null;
		try{
			httpClient = HttpClients.custom().disableAutomaticRetries().build();

			// parse response
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() != 200) {
				XxlJobLogger.log("Http StatusCode({}) Invalid.", response.getStatusLine().getStatusCode());
				return FAIL;
			}
			if (null == entity) {
				XxlJobLogger.log("Http Entity Empty.");
				return FAIL;
			}

			String responseMsg = EntityUtils.toString(entity, "UTF-8");
			XxlJobLogger.log(responseMsg);
			EntityUtils.consume(entity);
			return SUCCESS;
		} catch (Exception e) {
			XxlJobLogger.log(e);
			return FAIL;
		} finally{
			if (httpGet!=null) {
				httpGet.releaseConnection();
			}
			if (httpClient!=null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					XxlJobLogger.log(e);
				}
			}
		}
	}

}
