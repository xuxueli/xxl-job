package com.xxl.job.core.util;

import com.xxl.job.core.biz.model.ReturnT;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xuxueli 2017-05-10 21:28:15
 */
public class AdminApiUtil {
	private static Logger logger = LoggerFactory.getLogger(AdminApiUtil.class);

	public static final String CALLBACK = "/api/callback";
	public static final String REGISTRY = "/api/registry";

	private static List<String> adminAddressList = null;
	public static void init(String adminAddresses){
		// admin assress list
		if (adminAddresses != null) {
			Set<String> adminAddressSet = new HashSet<String>();
			for (String adminAddressItem: adminAddresses.split(",")) {
				if (adminAddressItem.trim().length()>0) {
					adminAddressSet.add(adminAddressItem);
				}
			}
            adminAddressList = new ArrayList<String>(adminAddressSet);
		}
	}
	public static boolean allowCallApi(){
        boolean allowCallApi = (adminAddressList!=null && adminAddressList.size()>0);
        return allowCallApi;
    }

	public static ReturnT<String> callApiFailover(String subUrl, Object requestObj) throws Exception {

		if (!allowCallApi()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "allowCallApi fail.");
		}

		for (String adminAddress: adminAddressList) {
			ReturnT<String> registryResult = null;
			try {
				String apiUrl = adminAddress.concat(subUrl);
				registryResult = callApi(apiUrl, requestObj);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (registryResult!=null && registryResult.getCode()==ReturnT.SUCCESS_CODE) {
				return ReturnT.SUCCESS;
			}
		}
		return ReturnT.FAIL;
	}

	public static ReturnT<String> callApi(String finalUrl, Object requestObj) throws Exception {
		HttpPost httpPost = new HttpPost(finalUrl);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {

			// timeout
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(10000)
					.setSocketTimeout(10000)
					.setConnectTimeout(10000)
					.build();

			httpPost.setConfig(requestConfig);

			// data
			if (requestObj != null) {
				String json = JacksonUtil.writeValueAsString(requestObj);

				StringEntity entity = new StringEntity(json, "utf-8");
				entity.setContentEncoding("UTF-8");
				entity.setContentType("application/json");

				httpPost.setEntity(entity);
			}

			// do post
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				if (response.getStatusLine().getStatusCode() != 200) {
					EntityUtils.consume(entity);
					return ReturnT.FAIL;
				}

				String responseMsg = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);
				if (responseMsg!=null && responseMsg.startsWith("{")) {
					ReturnT<String> result = JacksonUtil.readValue(responseMsg, ReturnT.class);
					return result;
				}
			}
			return ReturnT.FAIL;
		} catch (Exception e) {
			logger.error("", e);
			return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
		} finally {
			if (httpPost!=null) {
				httpPost.releaseConnection();
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
