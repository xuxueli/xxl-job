package com.xuxueli.executor.sample.nutz.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.concurrent.TimeUnit;

/**
 * 跨平台Http任务
 *
 * @author xuxueli 2018-09-16 03:48:34
 */
@JobHandler(value="httpJobHandler")
@IocBean
public class HttpJobHandler extends IJobHandler {

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		// valid
		if (param==null || param.trim().length()==0) {
			XxlJobLogger.log("URL Empty");
			return FAIL;
		}

		// httpclient
		HttpClient httpClient = null;
		try {
			httpClient = new HttpClient();
			httpClient.setFollowRedirects(false);	// Configure HttpClient, for example:
			httpClient.start();						// Start HttpClient

			// request
			Request request = httpClient.newRequest(param);
			request.method(HttpMethod.GET);
			request.timeout(5000, TimeUnit.MILLISECONDS);

			// invoke
			ContentResponse response = request.send();
			if (response.getStatus() != HttpStatus.OK_200) {
				XxlJobLogger.log("Http StatusCode({}) Invalid.", response.getStatus());
				return FAIL;
			}

			String responseMsg = response.getContentAsString();
			XxlJobLogger.log(responseMsg);
			return SUCCESS;
		} catch (Exception e) {
			XxlJobLogger.log(e);
			return FAIL;
		} finally {
			if (httpClient != null) {
				httpClient.stop();
			}
		}

	}

}
