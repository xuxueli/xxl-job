package com.dianping.job.client.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * http util to send hex data
 * @author xuxueli
 * @version  2015-11-28 15:30:59
 */
public class HttpUtil {

	public static String sendHex(String reqURL, String queryString) {

		String responseContent = null;
		if (queryString != null && !queryString.equals("")) {
			reqURL = reqURL + "?data=" + queryString;
		}

		HttpGet httpGet = new HttpGet(reqURL);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
			httpGet.setConfig(requestConfig);
			
			HttpResponse response = httpClient.execute(httpGet);
			
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);
				if (responseContent!=null) {
					responseContent = responseContent.trim();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseContent;
	}

}
