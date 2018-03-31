package com.xxl.job.admin.core.util;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class DingDingUtil {
	public static void send(String msg) {
		try {
			HttpClient httpclient = HttpClients.createDefault();

			HttpPost httppost = new HttpPost(
					"https://oapi.dingtalk.com/robot/send?access_token=16ebab541888b74a493fd3e4b201bc294a8b20904db5f09255901246031c4656");// I18nUtil.getString("dingding_webhook")
			httppost.addHeader("Content-Type", "application/json; charset=utf-8");

			StringEntity se = new StringEntity(msg, "utf-8");
			httppost.setEntity(se);
			httpclient.execute(httppost);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
