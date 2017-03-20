package com.xxl.job.executor.service.jobhandler;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;

@JobHander(value="3bJobHandler")
@Service
public class BabybusJobHandler extends IJobHandler{
	private static String url = "http://10.1.14.25:8000/console/index/m/syncdata";
	private static transient Logger log3 =  LoggerFactory.getLogger(BabybusJobHandler.class);
	
	@Override
	public void execute(String... params) throws Exception {
		// TODO Auto-generated method stub
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			
			// 发送get请求
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			
			// 请求发送成功，并得到结果
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				log3.info("get请求结果:" + response.toString());
			} else {
				log3.error("get请求提交失败:" + url);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
