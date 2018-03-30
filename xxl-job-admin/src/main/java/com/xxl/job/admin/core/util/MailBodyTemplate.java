package com.xxl.job.admin.core.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

public class MailBodyTemplate {

	public static StringBuffer template(StringBuffer data,Boolean start,Boolean end){
		if (start) {
			data.append("<h5>任务周期性监控异常明细：</span>");
			data.append("<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n");
			data.append("<thead style=\"font-family:微软雅黑; font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >");
			data.append("<tr>\n");
			data.append("<td>任务ID</td>\n");
			data.append("<td>任务描述</td>\n");
			data.append("<td>成功数</td>\n");
			data.append("<td>失败数</td>\n");
			data.append("<td>进行中</td>\n");
			data.append("</tr>\n");
			data.append("<thead/>\n");
		}
		
		if (end) {
			data.append( "</table>");
		}
		return data;
	}
	
	public static StringBuffer data(List<Map<String, Object>> triggerYesterdayMapAll,StringBuffer data,String desc){
		if (CollectionUtils.isNotEmpty(triggerYesterdayMapAll)) {
			data.append("<tbody>\n<tr>\n<td colspan=\"5\" style=\"font-family:微软雅黑;font-weight: bold;color: #ffffff;background-color: #4bccb4;\">"+desc+"</td>\n</tr>\n<tbody>\n");
			for (Map<String, Object> item: triggerYesterdayMapAll) {
				data.append("<tbody>\n<tr>\n");
				int triggerDayCount = Integer.valueOf(String.valueOf(item.get("triggerDayCount")));
				int triggerDayCountRunning = Integer.valueOf(String.valueOf(item.get("triggerDayCountRunning")));
				int triggerDayCountSuc = Integer.valueOf(String.valueOf(item.get("triggerDayCountSuc")));
				int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;
				
				data.append("<td>"+item.get("jobId")+"</td>\n");
				data.append("<td>"+item.get("jobName")+"</td>\n");
				data.append("<td>"+triggerDayCountSuc+"</td>\n");
				data.append("<td>"+triggerDayCountFail+"</td>\n");
				data.append("<td>"+triggerDayCountRunning+"</td>\n");
				data.append("<tbody>\n");
			}
			data.append("<tbody>\n<tr>\n<td colspan=\"6\"></td>\n</tr>\n<tbody>\n");
		}
		
		return data;
	}
	
}
