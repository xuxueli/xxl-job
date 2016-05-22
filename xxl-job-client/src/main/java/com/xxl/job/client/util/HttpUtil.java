package com.xxl.job.client.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

/**
 * http util to send data
 * @author xuxueli
 * @version  2015-11-28 15:30:59
 */
public class HttpUtil {
	
	/**
	 * http remote callback
	 */
	public static class RemoteCallBack{
		public static final String SUCCESS = "SUCCESS";
		public static final String FAIL = "FAIL";
		
		private String status;
		private String msg;
		public void setStatus(String status) {
			this.status = status;
		}
		public String getStatus() {
			return status;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getMsg() {
			return msg;
		}
		
		@Override
		public String toString() {
			return "RemoteCallBack [status=" + status + ", msg=" + msg + "]";
		}
		
	}
	
	/**
	 * http post request
	 * @param reqURL
	 * @param params
	 * @return	[0]=responseMsg, [1]=exceptionMsg
	 */
	public static RemoteCallBack post(String reqURL, Map<String, String> params){
		RemoteCallBack callback = new RemoteCallBack();
		callback.setStatus(RemoteCallBack.FAIL);
		
		// do post
		HttpPost httpPost = null;
		CloseableHttpClient httpClient = null;
		try{
			httpPost = new HttpPost(reqURL);
			httpClient = HttpClients.createDefault();
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
			if (response.getStatusLine().getStatusCode() == 200) {
				if (null != entity) {
					String responseMsg = EntityUtils.toString(entity, "UTF-8");
					callback = JacksonUtil.readValue(responseMsg, RemoteCallBack.class);
					if (callback == null) {
						callback = new RemoteCallBack();
						callback.setStatus(RemoteCallBack.FAIL);
						callback.setMsg("responseMsg parse json fail, responseMsg:" + responseMsg);
					}
					EntityUtils.consume(entity);
				}
			} else {
				callback.setMsg("http statusCode error, statusCode:" + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			callback.setMsg(out.toString());
		} finally{
			if (httpPost!=null) {
				httpPost.releaseConnection();
			}
			if (httpClient!=null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return callback;
	}
	
	/**
	 * parse address ip:port to url http://.../ 
	 * @param address
	 * @return
	 */
	public static String addressToUrl(String address){
		return "http://" + address + "/";
	}
	
}
